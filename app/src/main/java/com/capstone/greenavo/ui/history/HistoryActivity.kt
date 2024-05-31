package com.capstone.greenavo.ui.history

import android.content.res.Configuration
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.capstone.greenavo.R
import com.capstone.greenavo.data.ResultHistory
import com.capstone.greenavo.databinding.ActivityHistoryBinding
import com.capstone.greenavo.ui.adapter.HistoryDetectionAdapter

class HistoryActivity : AppCompatActivity() {
    private var _binding: ActivityHistoryBinding? = null
    private val binding get() = _binding!!

    private val list = ArrayList<ResultHistory>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityHistoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.ivBack.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        binding.rvHistory.setHasFixedSize(true)
        showRecyclerViewHistory()

        list.addAll(getListHistoryDeteksi())
    }

    private fun getListHistoryDeteksi(): ArrayList<ResultHistory> {
        val hasilGambar = resources.getStringArray(R.array.gambar_hasil_deteksi)
        val hasilNamaAlpukat = resources.getStringArray(R.array.hasil_nama_alpukat)
        val hasilKematangan= resources.getStringArray(R.array.hasil_kematangan)
        val hasilSkor= resources.getStringArray(R.array.hasil_skor)
        val listHistory = ArrayList<ResultHistory>()
        for (i in hasilNamaAlpukat.indices){
            val history = ResultHistory(
                hasilGambar[i],
                hasilNamaAlpukat[i],
                hasilKematangan[i],
                hasilSkor[i]
            )
            listHistory.add(history)
        }
        return listHistory
    }

    private fun showRecyclerViewHistory() {
        binding.rvHistory.layoutManager = LinearLayoutManager(this)
        val listHistoryDetectionAdapter = HistoryDetectionAdapter(list)
        binding.rvHistory.adapter = listHistoryDetectionAdapter

        if (applicationContext.resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE){
            binding.rvHistory.layoutManager = GridLayoutManager(this, 2)
        } else {
            binding.rvHistory.layoutManager = LinearLayoutManager(this)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}