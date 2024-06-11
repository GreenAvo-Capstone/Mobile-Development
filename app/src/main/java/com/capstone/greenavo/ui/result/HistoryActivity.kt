package com.capstone.greenavo.ui.result

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.recyclerview.widget.LinearLayoutManager
import com.capstone.greenavo.data.ResultHistory
import com.capstone.greenavo.databinding.ActivityHistoryBinding
import com.capstone.greenavo.adapter.HistoryDetectionAdapter
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.firestore

class HistoryActivity : AppCompatActivity() {
    private var _binding: ActivityHistoryBinding? = null
    private val binding get() = _binding!!

    private val list = ArrayList<ResultHistory>()

    private lateinit var db: FirebaseFirestore
    private lateinit var userId: String

    private lateinit var resultDetectionAdapter: HistoryDetectionAdapter
    private val listHasilDeteksi: MutableList<ResultHistory> = mutableListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityHistoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.ivBack.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        db = Firebase.firestore
        // Mendapatkan ID pengguna saat ini (misalnya, dari Firebase Authentication)
        userId = FirebaseAuth.getInstance().currentUser?.uid ?: "default_user_id"

        // Initialize RecyclerView
        binding.rvHistory.layoutManager = LinearLayoutManager(this)
        resultDetectionAdapter = HistoryDetectionAdapter(listHasilDeteksi)
        binding.rvHistory.adapter = resultDetectionAdapter

        reviewDataResult()
    }

    private fun reviewDataResult() {
        db.collection("users")
            .document(userId) // Menggunakan ID pengguna yang sesuai
            .collection("hasil_deteksi")
            .get()
            .addOnSuccessListener { result ->
                listHasilDeteksi.clear()
                for (document in result) {
                    val hasilDeteksi = document.toObject(ResultHistory::class.java)
                    hasilDeteksi.id = document.id
                    listHasilDeteksi.add(hasilDeteksi)
                }
                resultDetectionAdapter.notifyDataSetChanged()
            }
            .addOnFailureListener { exception ->
                Log.w("HistoryActivity", "Error getting documents.", exception)
            }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}