package com.capstone.greenavo.ui.resultdetection

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.ImageDecoder
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.lifecycleScope
import com.capstone.greenavo.R
import com.capstone.greenavo.databinding.ActivityResultDetectionBinding
import com.capstone.greenavo.databinding.LayoutSuccessBinding
import com.capstone.greenavo.ml.AvocadoClassification
import com.capstone.greenavo.ui.rekomendasi.RekomendasiActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.tensorflow.lite.DataType
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer
import java.io.InputStream
import java.nio.ByteBuffer
import java.nio.ByteOrder

class ResultDetectionActivity : AppCompatActivity() {
    private var _binding: ActivityResultDetectionBinding? = null
    private val binding get() = _binding!!

    val imageSize = 224
    private lateinit var resize: Bitmap

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityResultDetectionBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.ivBack.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        binding.btnSave.setOnClickListener {
            showPopupSimpan()

            //Fungsi Rekomendasi
            displayRekomendasi()
        }

        binding.rekomendasi.setOnClickListener {
            val intent = Intent(this, RekomendasiActivity::class.java)
            startActivity(intent)
        }

        resultImage()
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

    private fun displayImage(uri: Uri) {
        Log.d(TAG, "Displaying image: $uri")
        binding.hasilDeteksi.setImageURI(uri)

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

    private fun showPopupSimpan() {
        val dialogSuccessBinding = LayoutSuccessBinding.inflate(layoutInflater)

        dialogSuccessBinding.tvSuccess.text = "Menyimpan data berhasil!"

        val dialog = AlertDialog.Builder(this)
            .setView(dialogSuccessBinding.root)
            .setCancelable(false)
            .create()

        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        dialogSuccessBinding.btnOk.setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()
    }

    //Fungsi menampil textview Rekomendasi
    private fun displayRekomendasi() {
        val label = binding.hasilKematangan.text.toString()
        val visibility = View.VISIBLE
        val hidden = View.GONE
        val text = getString(R.string.rekomendasi)
        //Periksa label dan perbarui textview yang sesuai
        if (label == "Belum Matang"){
            binding.rekomendasi.visibility = visibility
            binding.rekomendasi.text = ""
        } else if (label == "Setengah Matang" || label == "Matang"){
            binding.rekomendasi.visibility = visibility
            binding.rekomendasi.text = text
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