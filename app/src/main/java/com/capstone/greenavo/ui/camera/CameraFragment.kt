package com.capstone.greenavo.ui.camera

import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.capstone.greenavo.R
import com.capstone.greenavo.databinding.FragmentCameraBinding
import com.capstone.greenavo.databinding.LayoutFailedBinding
import com.capstone.greenavo.databinding.LayoutKonfirmasiBinding
import com.capstone.greenavo.databinding.LayoutSuccessBinding
import com.capstone.greenavo.ui.resultdetection.ResultDetectionActivity
import com.capstone.greenavo.utils.getImageUri
import com.yalantis.ucrop.UCrop
import java.io.File
import kotlin.random.Random

class CameraFragment : Fragment() {

    private var currentImageUri: Uri? = null
    private var croppedImageUri: Uri? = null

    private var _binding: FragmentCameraBinding? = null
    private val binding get() = _binding!!

    private var loadingDialog: AlertDialog? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentCameraBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.btnCamera.setOnClickListener {
            //Fungsi camera
            startCamera()
        }

        binding.btnGallery.setOnClickListener {
            //Fungsi Galeri
            startGallery()
        }

        binding.btnScan.setOnClickListener {
            currentImageUri?.let {
                //Fungsi popup konfirmasi
                showPopupKonfirmasi()
            } ?: run {
                showToast(getString(R.string.image_classifier_failed))
            }

        }
    }

    //Camera
    private fun startCamera(){
        currentImageUri = getImageUri(requireActivity())
        launcherIntentCamera.launch(currentImageUri)
    }

    private val launcherIntentCamera = registerForActivityResult(
        ActivityResultContracts.TakePicture()
    ) { isSuccess ->
        if (isSuccess) {
            currentImageUri?.let { uri ->
                showImage()
                startUCrop(uri)
            }?: showToast("Failed to capture image URI")
        }
    }

    //Galeri
    private fun startGallery() {
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.type = "image/*"
        val chooser = Intent.createChooser(intent, "Choose a Picture")
        launcherIntentGallery.launch(chooser)
    }

    private val launcherIntentGallery = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == AppCompatActivity.RESULT_OK) {
            val selectedImg = result.data?.data
            selectedImg?.let { uri ->
                currentImageUri = uri
                showImage()
                startUCrop(uri)
            } ?: showToast("Failed to get image URI")
        }
    }

    private fun startUCrop(sourceUri: Uri) {
        val fileName = "cropped_image_${System.currentTimeMillis()}.jpg"
        val destinationUri = Uri.fromFile(File(requireContext().cacheDir, fileName))
        UCrop.of(sourceUri, destinationUri)
            .withAspectRatio(1f, 1f)
            .withMaxResultSize(1000, 1000)
            .start(requireContext(), this)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == UCrop.REQUEST_CROP && resultCode == AppCompatActivity.RESULT_OK) {
            val resultUri = UCrop.getOutput(data!!)
            resultUri?.let {
                showCroppedImage(resultUri)
            } ?: showToast("Failed to crop image")
        } else if (resultCode == UCrop.RESULT_ERROR) {
            val cropError = UCrop.getError(data!!)
            showToast("Crop error: ${cropError?.message}")
        }
    }

    private fun showImage() {
        currentImageUri?.let { uri ->
            Log.d(TAG, "Displaying image: $uri")
            binding.hasilGambar.setImageURI(uri)
        } ?: Log.d(TAG, "No image to display")
    }

    private fun showToast(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }

    private fun showCroppedImage(uri: Uri) {
        binding.hasilGambar.setImageURI(uri)
        croppedImageUri = uri
    }

    //Popup konfirmai
    private fun showPopupKonfirmasi() {
        val dialogKonfirmasiBinding = LayoutKonfirmasiBinding.inflate(layoutInflater)

        dialogKonfirmasiBinding.tvKonfirmasi.text = "Apakah ingin melakukan scan ?"

        val dialog = AlertDialog.Builder(requireContext())
            .setView(dialogKonfirmasiBinding.root)
            .setCancelable(false)
            .create()
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        dialogKonfirmasiBinding.btnYes.setOnClickListener {
            showLoading()
            dialog.dismiss()

            Handler(Looper.getMainLooper()).postDelayed({
                hideLoading()
                showPopupBerhasil()
            }, 2000)
        }

        dialogKonfirmasiBinding.btnNo.setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()
    }

    //Loading
    private fun showLoading() {
        if (loadingDialog == null) {
            val inflater = LayoutInflater.from(requireContext())
            val loadingView = inflater.inflate(R.layout.layout_loading, null)

            loadingDialog = AlertDialog.Builder(requireContext())
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

    //Popup berhasil
    private fun showPopupBerhasil() {
        val dialogSuccessBinding = LayoutSuccessBinding.inflate(layoutInflater)

        dialogSuccessBinding.tvSuccess.text = "Deteksi berhasil!"

        val dialog = AlertDialog.Builder(requireContext())
            .setView(dialogSuccessBinding.root)
            .setCancelable(false)
            .create()

        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        dialogSuccessBinding.btnOk.setOnClickListener {
            dialog.dismiss()
            analyzeImage()
        }

        dialog.show()
    }

    //Analisis gambar pindah ke halaman Result
    private fun analyzeImage() {
        val intent = Intent(requireContext(), ResultDetectionActivity::class.java)
        croppedImageUri?.let { uri ->
            intent.putExtra(ResultDetectionActivity.EXTRA_IMAGE_URI, uri.toString())
            startActivityForResult(intent, REQUEST_RESULT)
        } ?: showToast(getString(R.string.image_classifier_failed))
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        const val TAG = "ImagePicker"
        private const val REQUEST_RESULT = 1001
    }
}