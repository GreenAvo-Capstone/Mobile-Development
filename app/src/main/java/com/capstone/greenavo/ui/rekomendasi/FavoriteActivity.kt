package com.capstone.greenavo.ui.rekomendasi

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.recyclerview.widget.LinearLayoutManager
import com.capstone.greenavo.adapter.RekomendasiAlpukatAdapter
import com.capstone.greenavo.data.RekomendasiAlpukat
import com.capstone.greenavo.databinding.ActivityFavoriteBinding
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore

class FavoriteActivity : AppCompatActivity() {
    private var _binding: ActivityFavoriteBinding? = null
    private val binding get() = _binding!!

    //List
    private val listFavorite: MutableList<RekomendasiAlpukat> = mutableListOf()

    private lateinit var rekomendasiAlpukatAdapter: RekomendasiAlpukatAdapter

    //Firebase
    private lateinit var db: FirebaseFirestore
    private lateinit var userId: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityFavoriteBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.ivBack.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        db = Firebase.firestore
        // Mendapatkan ID pengguna saat ini (misalnya, dari Firebase Authentication)
        userId = FirebaseAuth.getInstance().currentUser?.uid ?: "default_user_id"

        // Initialize RecyclerView
        binding.rvFavorite.layoutManager = LinearLayoutManager(this)
        rekomendasiAlpukatAdapter = RekomendasiAlpukatAdapter(listFavorite)
        binding.rvFavorite.adapter = rekomendasiAlpukatAdapter

        reviewDataFavorite()
    }

    private fun reviewDataFavorite() {
        db.collection("users")
            .document(userId) // Menggunakan ID pengguna yang sesuai
            .collection("favorite")
            .get()
            .addOnSuccessListener { result ->
                listFavorite.clear()
                for (document in result) {
                    val favorite = document.toObject(RekomendasiAlpukat::class.java)
                    favorite.id = document.id
                    listFavorite.add(favorite)
                }
                rekomendasiAlpukatAdapter.notifyDataSetChanged()
            }
            .addOnFailureListener { exception ->
                Log.w("Favorite Acti", "Error getting documents.", exception)
            }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}