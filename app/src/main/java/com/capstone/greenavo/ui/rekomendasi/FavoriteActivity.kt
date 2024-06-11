package com.capstone.greenavo.ui.rekomendasi

import android.content.res.Configuration
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.capstone.greenavo.data.RekomendasiAlpukat
import com.capstone.greenavo.databinding.ActivityFavoriteBinding
import com.capstone.greenavo.adapter.RekomendasiAlpukatAdapter

class FavoriteActivity : AppCompatActivity() {
    private var _binding: ActivityFavoriteBinding? = null
    private val binding get() = _binding!!

    //List
    private val list = ArrayList<RekomendasiAlpukat>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityFavoriteBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.ivBack.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        binding.rvFavorite.setHasFixedSize(true)
        showRecyclerViewFavorite()
    }

    private fun showRecyclerViewFavorite() {
        binding.rvFavorite.layoutManager = LinearLayoutManager(this)
        val listRekomendasiAlpukatAdapter = RekomendasiAlpukatAdapter(list)
        binding.rvFavorite.adapter = listRekomendasiAlpukatAdapter

        if (applicationContext.resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE){
            binding.rvFavorite.layoutManager = GridLayoutManager(this, 2)
        } else {
            binding.rvFavorite.layoutManager = LinearLayoutManager(this)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}