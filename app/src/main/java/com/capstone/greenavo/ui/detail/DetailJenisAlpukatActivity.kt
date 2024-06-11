package com.capstone.greenavo.ui.detail

import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.bumptech.glide.Glide
import com.capstone.greenavo.data.JenisAlpukat
import com.capstone.greenavo.databinding.ActivityDetailJenisAlpukatBinding

class DetailJenisAlpukatActivity : AppCompatActivity() {
    private var _binding: ActivityDetailJenisAlpukatBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityDetailJenisAlpukatBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Setup toolbar
        binding.ivBack.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}