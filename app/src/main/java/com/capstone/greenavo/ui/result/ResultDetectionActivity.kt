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
import com.capstone.greenavo.ml.ModelGreenavo
import com.capstone.greenavo.ui.rekomendasi.RekomendasiActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
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

    private lateinit var resize: Bitmap
    private var loadingDialog: AlertDialog? = null

    private val imageSize = 224 // Size of the image expected by your model

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

    private fun resultImage() {
        val imageUriString = intent.getStringExtra(EXTRA_IMAGE_URI)
        if (imageUriString != null) {
            val imageUri = Uri.parse(imageUriString)
            displayImage(imageUri)
            classifyImage()
        } else {
            Log.e(TAG, "No image URI provided")
            finish()
        }
    }

    private fun displayImage(uri: Uri) {
        Log.d(TAG, "Displaying image: $uri")
        binding.ivHasilDeteksi.setImageURI(uri)

        try {
            val bitmap = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                val source = ImageDecoder.createSource(contentResolver, uri)
                ImageDecoder.decodeBitmap(source) { decoder, _, _ ->
                    decoder.setAllocator(ImageDecoder.ALLOCATOR_SOFTWARE)
                }
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

    private fun classifyImage() {
        lifecycleScope.launch(Dispatchers.Main) {
            try {
                val model = ModelGreenavo.newInstance(this@ResultDetectionActivity)

                // Creates inputs for reference.
                val inputFeature0 = TensorBuffer.createFixedSize(intArrayOf(1, imageSize, imageSize, 3), DataType.FLOAT32)

                val intValues = IntArray(resize.width * resize.height)
                resize.getPixels(intValues, 0, resize.width, 0, 0, resize.width, resize.height)

                val byteBuffer = ByteBuffer.allocateDirect(4 * imageSize * imageSize * 3)
                byteBuffer.order(ByteOrder.nativeOrder())

                var pixel = 0

                for (i in 0 until imageSize) {
                    for (j in 0 until imageSize) {
                        val `val` = intValues[pixel++]
                        byteBuffer.putFloat(((`val` shr 16) and 0xFF).toFloat() * (1f / 255f))
                        byteBuffer.putFloat(((`val` shr 8) and 0xFF).toFloat() * (1f / 255f))
                        byteBuffer.putFloat((`val` and 0xFF).toFloat() * (1f / 255f))
                    }
                }

                inputFeature0.loadBuffer(byteBuffer)

                // Runs model inference and gets result.
                val outputs = model.process(inputFeature0)
                val outputFeature0 = outputs.outputFeature0AsTensorBuffer

                val confidences = outputFeature0.floatArray

                val labelMap = hashMapOf(
                    0 to "Breaking",
                    2 to "Overripe",
                    3 to "RipeFS",
                    4 to "RipeSS",
                    5 to "Underripe"
                )

                var maxPos = 0
                var maxConfidence = 0f

                for (i in confidences.indices) {
                    if (confidences[i] > maxConfidence) {
                        maxConfidence = confidences[i]
                        maxPos = i
                    }
                }

                val label = labelMap[maxPos]

                fun Float.formatToString(): String {
                    return String.format("%.2f%%", this * 100)
                }

                val color = when (label) {
                    "Breaking" -> R.color.breaking
                    "Overripe" -> R.color.overripe
                    "RipeFS" -> R.color.ripefs
                    "RipeSS" -> R.color.ripess
                    "Underripe" -> R.color.underripe
                    else -> android.R.color.black
                }

                withContext(Dispatchers.Main) {
                    binding.hasilKematangan.text = label
                    binding.hasilKematangan.setTextColor(resources.getColor(color, null))
                    binding.hasilSkor.text = maxConfidence.formatToString()
                    binding.isiDeskripisi.text = "The maturity level results are $label with score ${maxConfidence.formatToString()}"
                }

                model.close()

            } catch (e: Exception) {
                Log.e(TAG, "Error during image processing", e)
            }
        }
    }

    private fun saveDataDetection() {
        val imageView = binding.ivHasilDeteksi
        val label = binding.hasilKematangan.text.toString()
        val score = binding.hasilSkor.text.toString()

        val storage = FirebaseStorage.getInstance()
        val db = FirebaseFirestore.getInstance()
        val currentUserId = FirebaseAuth.getInstance().currentUser?.uid ?: "anonymous_user"

        binding.rekomendasi.apply {
            visibility = View.VISIBLE
            text = if (label == "Underripe" || label == "Overripe") "" else getString(R.string.rekomendasi)
        }

        val bitmap = (imageView.drawable as BitmapDrawable).bitmap
        val baos = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)
        val data = baos.toByteArray()

        showLoading()

        val imageFileName = "images/${currentUserId}_${System.currentTimeMillis()}_hasilDeteksiImages.jpg"
        val imageRef = storage.reference.child(imageFileName)

        val uploadTask = imageRef.putBytes(data)
        uploadTask.addOnSuccessListener {
            imageRef.downloadUrl.addOnSuccessListener { uri ->
                hideLoading()

                val imageUrl = uri.toString()

                val detectionResult = hashMapOf(
                    "image_url" to imageUrl,
                    "label" to label,
                    "score" to score
                )

                db.collection("users").document(currentUserId)
                    .collection("hasil_deteksi")
                    .add(detectionResult)
                    .addOnSuccessListener { documentReference ->
                        showPopupSimpan("Hasil Deteksi telah disimpan!")
                        Log.d(TAG, "DocumentSnapshot added with ID: ${documentReference.id}")
                    }
                    .addOnFailureListener { e ->
                        showPopupGagal("Gagal menyimpan hasil deteksi")
                        Log.w(TAG, "Error adding document", e)
                    }
            }.addOnFailureListener { exception ->
                Toast.makeText(this@ResultDetectionActivity, "Error getting download URL: ${exception.message}", Toast.LENGTH_SHORT).show()
                Log.w(TAG, "Error getting download URL", exception)
            }
        }.addOnFailureListener { exception ->
            Toast.makeText(this@ResultDetectionActivity, "Error uploading image: ${exception.message}", Toast.LENGTH_SHORT).show()
            Log.w(TAG, "Error uploading image", exception)
        }
    }

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

        dialog.show()
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    companion object {
        const val EXTRA_IMAGE_URI = "extra_image_uri"
        const val TAG = "ResultDetectionActivity"
    }
}
