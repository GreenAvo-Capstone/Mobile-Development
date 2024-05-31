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

        val listAlpukat = if (Build.VERSION.SDK_INT >= 33){
            intent.getParcelableExtra<JenisAlpukat>(KEY_JENIS_ALPUKAT, JenisAlpukat::class.java)
        } else {
            @Suppress("DEPRECATION")
            intent.getParcelableExtra<JenisAlpukat>(KEY_JENIS_ALPUKAT)
        }
        if (listAlpukat != null) {
            binding.barJenisAlpukat.text = listAlpukat.namaAlpukat
            binding.tvTitleJenisAlpukat.text = listAlpukat.namaAlpukat
            Glide.with(this).load(listAlpukat.gambarAlpukat).into(binding.ivDetailJenisAlpukat)
            binding.tvDeskripsiSatu.text = listAlpukat.deskripsiSatu
            binding.tvDeskripsiDua.text = listAlpukat.deskripsiDua
            binding.tvSumberLemakSehatSerat.text = listAlpukat.sumberLemakSehatSerat
            binding.tvKayaAkanNutrisi.text = listAlpukat.kayaAkanNutrisi
            binding.tvTinggiSerat.text = listAlpukat.tinggiSerat
            binding.tvAntiokesida.text = listAlpukat.antioksidan
            binding.tvManfaatKulit.text = listAlpukat.manfaatKulit
        }
    }

    companion object{
        const val KEY_JENIS_ALPUKAT = "key_equipment"
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}