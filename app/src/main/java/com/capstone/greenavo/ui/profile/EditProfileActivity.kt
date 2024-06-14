package com.capstone.greenavo.ui.profile

import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import com.bumptech.glide.Glide
import com.capstone.greenavo.R
import com.capstone.greenavo.databinding.ActivityEditProfileBinding
import com.capstone.greenavo.databinding.LayoutFailedBinding
import com.capstone.greenavo.databinding.LayoutKonfirmasiBinding
import com.capstone.greenavo.databinding.LayoutSuccessBinding
import com.capstone.greenavo.utils.getImageUri
import com.capstone.greenavo.utils.reduceFileImage
import com.capstone.greenavo.utils.uriToFile
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.yalantis.ucrop.UCrop
import java.io.File
import java.util.UUID

class EditProfileActivity : AppCompatActivity() {
    private var _binding: ActivityEditProfileBinding? = null
    private val binding get() = _binding!!

    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore
    private lateinit var storage: FirebaseStorage

    private var currentImageUri: Uri? = null
    private var croppedImageUri: Uri? = null

    private var loadingDialog: AlertDialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityEditProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = Firebase.auth
        db = FirebaseFirestore.getInstance()
        storage = FirebaseStorage.getInstance()

        fetchUserData()

        binding.btnCamera.setOnClickListener {
            showImageSourceDialog()
        }

        binding.btnKonfirmasi.setOnClickListener {
            showPopupKonfirmasi("Apakah anda yakin ingin mengubah data profil?")
        }

        binding.ivBack.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
    }

    private fun showImageSourceDialog() {
        val options = arrayOf("Camera", "Gallery")
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Choose an option")
        builder.setItems(options) { dialog, which ->
            when (which) {
                0 -> openCamera()
                1 -> openGallery()
            }
        }
        builder.show()
    }

    private fun openGallery() {
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.type = "image/*"
        val chooser = Intent.createChooser(intent, "Choose a Picture")
        launcherIntentGallery.launch(chooser)
    }

    private val launcherIntentGallery = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            val selectedImg = result.data?.data
            selectedImg?.let { uri ->
                currentImageUri = uri
                showImage()
                startUCrop(uri)
            } ?: showToast("Failed to get image URI")
        }
    }

    private fun openCamera() {
        currentImageUri = getImageUri(this)
        launcherIntentCamera.launch(currentImageUri!!)
    }

    private val launcherIntentCamera = registerForActivityResult(ActivityResultContracts.TakePicture()) { isSuccess ->
        if (isSuccess) {
            currentImageUri?.let { uri ->
                showImage()
                startUCrop(uri)
            } ?: showToast("Failed to get image URI")
        }
    }

    private fun startUCrop(sourceUri: Uri) {
        val fileName = "cropped_image_${System.currentTimeMillis()}.jpg"
        val destinationUri = Uri.fromFile(File(cacheDir, fileName))
        UCrop.of(sourceUri, destinationUri)
            .withAspectRatio(1f, 1f)
            .withMaxResultSize(1000, 1000)  // Adjust the size as needed
            .start(this)
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == UCrop.REQUEST_CROP && resultCode == RESULT_OK) {
            val resultUri = UCrop.getOutput(data!!)
            resultUri?.let {
                showCroppedImage(it)
                // After showing the cropped image, upload it to Firebase Storage
                uploadCroppedImage(it)
            } ?: showToast("Failed to crop image")
        } else if (resultCode == UCrop.RESULT_ERROR) {
            val cropError = UCrop.getError(data!!)
            showToast("Crop error: ${cropError?.message}")
        }
    }

    private fun showImage() {
        currentImageUri?.let { uri ->
            Log.d(TAG, "Displaying image: $uri")
            binding.ivImageProfile.setImageURI(uri)
        } ?: Log.d(TAG, "No image to display")
    }

    private fun showCroppedImage(uri: Uri) {
        binding.ivImageProfile.setImageURI(uri)
        croppedImageUri = uri
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    private fun fetchUserData() {
        val user = auth.currentUser
        val userId = user?.uid

        if (userId != null) {
            db.collection("users")
                .document(userId)
                .get()
                .addOnSuccessListener {document ->
                    if (document != null) {
                        val name = document.getString("name")
                        val nomortlp = document.getString("no_tlp")
                        val alamat = document.getString("alamat")

                        binding.etNama.setText(name)
                        binding.etNomorTelepon.setText(nomortlp)
                        binding.etAlamat.setText(alamat)

                        val fotoProfil = document.getString("foto_profil")
                        if (fotoProfil != null) {
                            Glide.with(this)
                                .load(fotoProfil)
                                .into(binding.ivImageProfile)
                        } else {
                            binding.ivImageProfile.setImageResource(R.drawable.logo_full_apk)
                        }
                    } else {
                        // Data tidak ditemukan, tampilkan pesan error atau lakukan tindakan lain
                        binding.etNama.error = ""
                        binding.etNomorTelepon.error = ""
                        binding.etAlamat.error = ""
                    }
                }
                .addOnFailureListener { exception ->
                    // Gagal mendapatkan data, tampilkan pesan error atau lakukan tindakan lain
                    Toast.makeText(this, "Error: ${exception.message}", Toast.LENGTH_SHORT).show()
                }
        } else {
            // Pengguna tidak terautentikasi, tampilkan pesan error atau lakukan tindakan lain
            Toast.makeText(this, "Pengguna tidak ditemukan", Toast.LENGTH_SHORT).show()
        }
    }

    //Potong gambar disesuaikan
    private fun uploadCroppedImage(uri: Uri) {
        val imageFile = uriToFile(uri, this).reduceFileImage()

        // Generate a unique name for the cropped image file
        val imageName = "cropped_${UUID.randomUUID()}.jpg"

        // Reference to Firebase Storage for the cropped image
        val storageRef = storage.reference.child("profile_images/$imageName")

        // Upload the cropped image to Firebase Storage
        storageRef.putFile(Uri.fromFile(imageFile))
            .addOnSuccessListener { taskSnapshot ->
                // Get the URL of the uploaded cropped image
                storageRef.downloadUrl.addOnSuccessListener { imageUrl ->
                    // Update Firestore document with the new image URL
                    showPopupGanti("Foto profil berhasil diubah")
                }
            }
            .addOnFailureListener { e ->
                // Failed to upload the cropped image
                Log.e(TAG, "Error uploading cropped image: ${e.message}")
            }
    }

    //Simpan data
    private fun actionSaveData(userId: String, imageUrl: String? = null) {
        showLoading()
        val user = hashMapOf<String, Any>(
            "name" to binding.etNama.text.toString(),
            "alamat" to binding.etAlamat.text.toString(),
            "no_tlp" to binding.etNomorTelepon.text.toString()
        )

        if (imageUrl != null) {
            user["foto_profil"] = imageUrl
        }

        db.collection("users")
            .document(userId)
            .update(user)
            .addOnSuccessListener {
                hideLoading()
                showPopupBerhasil("Data berhasil diubah")
            }
            .addOnFailureListener { e ->
                showPopupGagal("Data gagal diubah")
                Toast.makeText(this, "Error: $e", Toast.LENGTH_SHORT).show()
            }
    }

    //Popup gagal
    private fun showPopupGagal(message: String) {
        val dialogFailedBinding = LayoutFailedBinding.inflate(layoutInflater)

        dialogFailedBinding.tvFailed.text = message

        val dialog = AlertDialog.Builder(this@EditProfileActivity)
            .setView(dialogFailedBinding.root)
            .setCancelable(false)
            .create()

        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        dialogFailedBinding.btnOk.setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()
    }

    //Popup berhasil
    private fun showPopupBerhasil(message: String) {
        val dialogSuccessBinding = LayoutSuccessBinding.inflate(layoutInflater)

        dialogSuccessBinding.tvSuccess.text = message

        val dialog = AlertDialog.Builder(this@EditProfileActivity)
            .setView(dialogSuccessBinding.root)
            .setCancelable(false)
            .create()

        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        dialogSuccessBinding.btnOk.setOnClickListener {
            dialog.dismiss()
            finish()
        }

        dialog.show()
    }

    private fun showPopupGanti(message: String) {
        val dialogSuccessBinding = LayoutSuccessBinding.inflate(layoutInflater)

        dialogSuccessBinding.tvSuccess.text = message

        val dialog = AlertDialog.Builder(this@EditProfileActivity)
            .setView(dialogSuccessBinding.root)
            .setCancelable(false)
            .create()

        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        dialogSuccessBinding.btnOk.setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()
    }

    //Popup konfirmasi
    private fun showPopupKonfirmasi(message: String) {
        val dialogKonfirmasiBinding = LayoutKonfirmasiBinding.inflate(layoutInflater)
        dialogKonfirmasiBinding.tvKonfirmasi.text = message

        val dialog = AlertDialog.Builder(this@EditProfileActivity)
            .setView(dialogKonfirmasiBinding.root)
            .setCancelable(false)
            .create()

        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        dialogKonfirmasiBinding.btnYes.setOnClickListener {
            dialog.dismiss()
            actionSaveData(auth.currentUser?.uid.toString(), croppedImageUri.toString())
        }

        dialogKonfirmasiBinding.btnNo.setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()
    }

    //Loading
    private fun showLoading() {
        if (loadingDialog == null) {
            val inflater = LayoutInflater.from(this@EditProfileActivity)
            val loadingView = inflater.inflate(R.layout.layout_loading, null)

            loadingDialog = AlertDialog.Builder(this@EditProfileActivity)
                .setView(loadingView)
                .setCancelable(false)
                .create()

            loadingDialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        }

        loadingDialog?.show()
    }

    private fun hideLoading() {
        loadingDialog?.dismiss()
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    companion object {
        const val TAG = "ImagePicker"
    }
}