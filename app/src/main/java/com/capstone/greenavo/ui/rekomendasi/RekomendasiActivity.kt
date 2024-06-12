package com.capstone.greenavo.ui.rekomendasi

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.recyclerview.widget.LinearLayoutManager
import com.capstone.greenavo.adapter.RekomendasiAlpukatAdapter
import com.capstone.greenavo.data.RekomendasiAlpukat
import com.capstone.greenavo.databinding.ActivityRekomendasiBinding
import com.google.firebase.Firebase
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore

class RekomendasiActivity : AppCompatActivity() {
    private var _binding: ActivityRekomendasiBinding? = null
    private val binding get() = _binding!!

    //List
    private val listRekomendasi: MutableList<RekomendasiAlpukat> = mutableListOf()

    //Adapter
    private lateinit var rekomendasiAlpukatAdapter: RekomendasiAlpukatAdapter

    //Firebase
    private lateinit var db: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityRekomendasiBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //Inisialisasi Firebase
        db = Firebase.firestore

        //Inisialisasi RecyclerView
        binding.rvRekomendasi.layoutManager = LinearLayoutManager(this)
        rekomendasiAlpukatAdapter = RekomendasiAlpukatAdapter(listRekomendasi)
        binding.rvRekomendasi.adapter = rekomendasiAlpukatAdapter

        binding.ivBack.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        //Melihat data
        showDataRekomendasi()
    }

    private fun showDataRekomendasi() {
        db.collection("rekomendasi")
            .get()
            .addOnSuccessListener { result ->
                listRekomendasi.clear()
                for (document in result) {
                    val rekomendasi = document.toObject(RekomendasiAlpukat::class.java)
                    rekomendasi.id = document.id
                    listRekomendasi.add(rekomendasi)
                }
                rekomendasiAlpukatAdapter.notifyDataSetChanged()
            }
            .addOnFailureListener { exception ->
                Log.w("RekomendasiActivity", "Error getting documents: ", exception)
            }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}