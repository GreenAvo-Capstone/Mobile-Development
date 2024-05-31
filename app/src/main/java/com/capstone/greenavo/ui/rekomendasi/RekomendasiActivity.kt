package com.capstone.greenavo.ui.rekomendasi

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.capstone.greenavo.databinding.ActivityRekomendasiBinding

class RekomendasiActivity : AppCompatActivity() {
    private var _binding: ActivityRekomendasiBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityRekomendasiBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.ivBack.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}