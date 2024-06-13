package com.capstone.greenavo.ui.result

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.ImageDecoder
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.lifecycleScope
import com.capstone.greenavo.R
import com.capstone.greenavo.databinding.ActivityResultDetectionBinding
import com.capstone.greenavo.databinding.LayoutSuccessBinding
import com.capstone.greenavo.ml.AvocadoClassification
import com.capstone.greenavo.ui.rekomendasi.RekomendasiActivity
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.firestore
import com.google.firebase.storage.storage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.tensorflow.lite.DataType
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer
import java.io.ByteArrayOutputStream
import java.io.InputStream
import java.nio.ByteBuffer
import java.nio.ByteOrder

class ResultDetectionActivity : AppCompatActivity() {
    private var _binding: ActivityResultDetectionBinding? = null
    private val binding get() = _binding!!

    val imageSize = 224
    private lateinit var resize: Bitmap

    private var loadingDialog: AlertDialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityResultDetectionBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.ivBack.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        binding.btnSave.setOnClickListener {
            saveDataDetection()
        }

        binding.rekomendasi.setOnClickListener {
            val intent = Intent(this, RekomendasiActivity::class.java)
            startActivity(intent)
        }

        resultImage()
    }
    private fun saveDataDetection() {
        // Get references to the ImageView and TextView elements
        val imageView = binding.ivHasilDeteksi
        val label = binding.hasilKematangan.text.toString()
        val score = binding.hasilSkor.text.toString()

        // Define Firebase Storage and Firestore references
        val storage = Firebase.storage
        val db = Firebase.firestore

        val currentUserId = FirebaseAuth.getInstance().currentUser?.uid ?: "anonymous_user"

        // Make the view visible and set the recommendation text based on the label
        binding.rekomendasi.apply {
            visibility = View.VISIBLE
            text = if (label == "Belum Matang") "" else getString(R.string.rekomendasi)
        }

        // Convert ImageView to byte array
        val bitmap = (imageView.drawable as BitmapDrawable).bitmap
        val baos = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)
        val data = baos.toByteArray()

        showLoading()

        // Create a unique filename for the image
        val imageFileName = "images/${currentUserId}_${System.currentTimeMillis()}_hasilDeteksiImages.jpg"
        val imageRef = storage.reference.child(imageFileName)

        // Upload the image to Firebase Storage
        val uploadTask = imageRef.putBytes(data)
        uploadTask.addOnSuccessListener {
            // Get the download URL of the uploaded image
            imageRef.downloadUrl.addOnSuccessListener { uri ->

                hideLoading()

                val imageUrl = uri.toString()

                // Create a new document in the Firestore collection "hasil_deteksi"
                val detectionResult = hashMapOf(
                    "image_url" to imageUrl,
                    "label" to label,
                    "score" to score
                )

                db.collection("users").document(currentUserId)
                    .collection("hasil_deteksi")
                    .add(detectionResult)
                    .addOnSuccessListener { documentReference ->
                        // Show a Toast message when the document is successfully added
                        showPopupSimpan("Hasil Deteksi telah disimpan!")
                        Log.d(TAG, "DocumentSnapshot added with ID: ${documentReference.id}")
                    }
                    .addOnFailureListener { e ->
                        // Show a Toast message if adding the document fails
                        showPopupGagal("Gagal menyimpan hasil deteksi")
                        Log.w(TAG, "Error adding document", e)
                    }
            }.addOnFailureListener { exception ->
                // Show a Toast message if getting the download URL fails
                Toast.makeText(this, "Error getting download URL: ${exception.message}", Toast.LENGTH_SHORT).show()
                Log.w(TAG, "Error getting download URL", exception)
            }
        }.addOnFailureListener { exception ->
            // Show a Toast message if the image upload fails
            Toast.makeText(this, "Error uploading image: ${exception.message}", Toast.LENGTH_SHORT).show()
            Log.w(TAG, "Error uploading image", exception)
        }
    }

    // Hasil gambar, kematangan, dan skor
    private fun resultImage() {
        val imageUriString = intent.getStringExtra(EXTRA_IMAGE_URI)
        if (imageUriString != null) {
            val imageUri = Uri.parse(imageUriString)
            displayImage(imageUri)
            classifications()
        } else {
            Log.e(TAG, "No image URI provided")
            finish()
        }
    }

    private fun classifications() {
        lifecycleScope.launch(Dispatchers.IO) {
            try {
                // Model
                val model = AvocadoClassification.newInstance(this@ResultDetectionActivity)

                // Creates inputs for reference.
                val inputFeature0 = TensorBuffer.createFixedSize(intArrayOf(1, imageSize, imageSize, 3), DataType.FLOAT32)

                val intValues = IntArray(resize.width * resize.height)
                resize.getPixels(intValues, 0, resize.width, 0, 0, resize.width, resize.height)

                val byteBuffer = ByteBuffer.allocateDirect(4 * imageSize * imageSize * 3)
                byteBuffer.order(ByteOrder.nativeOrder())

                var pixel = 0

                for (i in 0 until imageSize) {
                    for (j in 0 until imageSize) {
                        val `val` = intValues[pixel++] // RGB
                        byteBuffer.putFloat(((`val` shr 16) and 0xFF).toFloat() * (1f / 255f))
                        byteBuffer.putFloat(((`val` shr 8) and 0xFF).toFloat() * (1f / 255f))
                        byteBuffer.putFloat((`val` and 0xFF).toFloat() * (1f / 255f))
                    }
                }

                inputFeature0.loadBuffer(byteBuffer)

                // Runs model inference and gets result.
                val outputs = model.process(inputFeature0)
                val outputFeature0 = outputs.outputFeature0AsTensorBuffer

                // Get labels and scores
                val confidences = outputFeature0.floatArray

                val labelMap = mapOf(
                    0 to "Belum Matang",
                    1 to "Setengah Matang",
                    2 to "Matang"
                )

                // Find the top prediction
                var maxPos = 0
                var maxConfidence = 0f
                for (i in confidences.indices) {
                    if (confidences[i] > maxConfidence) {
                        maxConfidence = confidences[i]
                        maxPos = i
                    }
                }

                // Get the label using the map
                val label = labelMap[maxPos] ?: "Unknown"

                fun Float.formatToString(): String {
                    return String.format("%.0f%%", this * 100)
                }

                // Determine the color based on the label
                val color = when (label) {
                    "Belum Matang" -> R.color.low
                    "Setengah Matang" -> R.color.medium
                    "Matang" -> R.color.high
                    else -> android.R.color.black // default color
                }

                // Display the results (e.g., in a TextView)
                withContext(Dispatchers.Main) {
                    binding.hasilKematangan.text = "$label"
                    binding.hasilKematangan.setTextColor(resources.getColor(color, null))
                    binding.hasilSkor.text = "${maxConfidence.formatToString()}"
                    binding.isiDeskripisi.text = "Hasil tingkat kematangan adalah ${label} dengan skor ${maxConfidence.formatToString()}"
                }

                // Releases model resources if no longer used.
                model.close()

            } catch (e: Exception) {
                Log.e("Error", "Error during image processing", e)
            }
        }
    }

    //Loading
    private fun showLoading() {
        if (loadingDialog == null) {
            val inflater = LayoutInflater.from(this@ResultDetectionActivity)
            val loadingView = inflater.inflate(R.layout.layout_loading, null)

            loadingDialog = AlertDialog.Builder(this@ResultDetectionActivity)
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

    private fun displayImage(uri: Uri) {
        Log.d(TAG, "Displaying image: $uri")
        binding.ivHasilDeteksi.setImageURI(uri)

        try {
            val bitmap = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                val source = ImageDecoder.createSource(this.contentResolver, uri)
                ImageDecoder.decodeBitmap(source)
            } else {
                val inputStream: InputStream? = contentResolver.openInputStream(uri)
                BitmapFactory.decodeStream(inputStream)
            }
            resize = Bitmap.createScaledBitmap(bitmap, imageSize, imageSize, true)
        } catch (e: Exception) {
            Log.e(TAG, "Error decoding image", e)
            finish()
        }
    }



    //Popup berhasil
    private fun showPopupSimpan(message: String) {
        val dialogSuccessBinding = LayoutSuccessBinding.inflate(layoutInflater)

        dialogSuccessBinding.tvSuccess.text = message

        val dialog = AlertDialog.Builder(this@ResultDetectionActivity)
            .setView(dialogSuccessBinding.root)
            .setCancelable(false)
            .create()

        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        dialogSuccessBinding.btnOk.setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()
    }

    //Popup gagal
    private fun showPopupGagal(message: String) {
        val dialogFailedBinding = LayoutSuccessBinding.inflate(layoutInflater)

        dialogFailedBinding.tvSuccess.text = message

        val dialog = AlertDialog.Builder(this@ResultDetectionActivity)
            .setView(dialogFailedBinding.root)
            .setCancelable(false)
            .create()

        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        dialogFailedBinding.btnOk.setOnClickListener {
            dialog.dismiss()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    companion object {
        const val EXTRA_IMAGE_URI = "extra_image_uri"
        const val TAG = "imagePicker"
    }
}