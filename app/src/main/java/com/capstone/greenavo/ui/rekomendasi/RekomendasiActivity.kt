package com.capstone.greenavo.ui.rekomendasi

import android.content.res.Configuration
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.capstone.greenavo.R
import com.capstone.greenavo.data.RekomendasiAlpukat
import com.capstone.greenavo.databinding.ActivityRekomendasiBinding
import com.capstone.greenavo.adapter.RekomendasiAlpukatAdapter

class RekomendasiActivity : AppCompatActivity() {
    private var _binding: ActivityRekomendasiBinding? = null
    private val binding get() = _binding!!

    //List
    private val list = ArrayList<RekomendasiAlpukat>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityRekomendasiBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.ivBack.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        binding.rvRekomendasi.setHasFixedSize(true)
        showRecyclerViewRekomendasi()

        list.addAll(getListRekomendasi())
    }
    private fun getListRekomendasi(): ArrayList<RekomendasiAlpukat> {
        val gambarRekomendasi = resources.getStringArray(R.array.gambar_rekomendasi)
        val namaRekomendasi = resources.getStringArray(R.array.nama_rekomendasi)
        val deskripsiRekomendasi = resources.getStringArray(R.array.deskripsi_rekomendasi)
        val listRekomendasi = ArrayList<RekomendasiAlpukat>()
        for (i in namaRekomendasi.indices){
            val rekomendasi = RekomendasiAlpukat(
                gambarRekomendasi[i],
                namaRekomendasi[i],
                deskripsiRekomendasi[i]
            )
            listRekomendasi.add(rekomendasi)
        }
        return listRekomendasi
    }

    private fun showRecyclerViewRekomendasi() {
        binding.rvRekomendasi.layoutManager = LinearLayoutManager(this)
        val listRekomendasiAlpukatAdapter = RekomendasiAlpukatAdapter(list)
        binding.rvRekomendasi.adapter = listRekomendasiAlpukatAdapter

        if (applicationContext.resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE){
            binding.rvRekomendasi.layoutManager = GridLayoutManager(this, 2)
        } else {
            binding.rvRekomendasi.layoutManager = LinearLayoutManager(this)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}