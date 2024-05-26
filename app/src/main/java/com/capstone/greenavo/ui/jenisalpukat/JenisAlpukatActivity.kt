package com.capstone.greenavo.ui.jenisalpukat

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.capstone.greenavo.databinding.ActivityJenisAlpukatBinding

class JenisAlpukatActivity : AppCompatActivity() {
    private var _binding: ActivityJenisAlpukatBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityJenisAlpukatBinding.inflate(layoutInflater)
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