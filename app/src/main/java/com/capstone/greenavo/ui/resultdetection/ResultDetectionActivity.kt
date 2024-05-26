package com.capstone.greenavo.ui.resultdetection

import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContentProviderCompat.requireContext
import com.capstone.greenavo.R
import com.capstone.greenavo.databinding.ActivityResultDetectionBinding
import com.capstone.greenavo.databinding.LayoutSuccessBinding
import com.capstone.greenavo.ui.resep.ResepAlpukatActivity

class ResultDetectionActivity : AppCompatActivity() {
    private var _binding: ActivityResultDetectionBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityResultDetectionBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.ivBack.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        binding.btnSave.setOnClickListener {
            showPopupBerhasil()
            displayRecommendation()
        }

        binding.rekomendasi.setOnClickListener {
            val intent = Intent(this, ResepAlpukatActivity::class.java)
            startActivity(intent)
        }
    }

    private fun showPopupBerhasil() {
        val dialogSuccessBinding = LayoutSuccessBinding.inflate(layoutInflater)

        dialogSuccessBinding.tvSuccess.text = "Deteksi berhasil!"

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

    private fun displayRecommendation() {
        val visibility = View.VISIBLE
        val text = getString(R.string.rekomendasi)
        // Menampilkan dan mengubah teks dari TextView
        binding.rekomendasi.apply {
            this.visibility = visibility
            this.text = text
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}