package com.capstone.greenavo.ui.resep

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.capstone.greenavo.R
import com.capstone.greenavo.databinding.ActivityResepAlpukatBinding

class ResepAlpukatActivity : AppCompatActivity() {
    private var _binding: ActivityResepAlpukatBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityResepAlpukatBinding.inflate(layoutInflater)
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