package com.capstone.greenavo.ui.detail

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.capstone.greenavo.databinding.ActivityDetailResepAlpukatBinding
import com.google.firebase.firestore.FirebaseFirestore

class DetailResepAlpukatActivity : AppCompatActivity() {
    private var _binding: ActivityDetailResepAlpukatBinding? = null
    private val binding get() = _binding!!

    private lateinit var db: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityDetailResepAlpukatBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Setup toolbar
        binding.ivBack.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        db = FirebaseFirestore.getInstance()

        // Get the document ID passed from the previous activity
        val documentId = intent.getStringExtra("DOCUMENT_ID")

        // Fetch the details from Firestore
        documentId?.let {
            fetchDetails(it)
        }
    }

    private fun fetchDetails(documentId: String) {
        db.collection("resep_alpukat").document(documentId)
            .get()
            .addOnSuccessListener { document ->
                if (document != null && document.exists()) {
                    // Retrieve the data from the document
                    val nama = document.getString("nama_resep") ?: ""
                    val gambar = document.getString("gambar") ?: ""
                    val deskripsi = document.getString("deskripsi") ?: ""

                    // Retrieve the array field
                    val bahanList = document.get("bahan") as? List<String> ?: arrayListOf()
                    val membuatList = document.get("cara_membuat") as? ArrayList<String> ?: arrayListOf()

                    // Populate the views with the data
                    binding.tvNama.text = nama
                    Glide.with(this).load(gambar).into(binding.ivGambar)
                    binding.tvDeskripsi.text = deskripsi

                    // Display the array data
                    val membuat = membuatList.joinToString("\n") { "$it" }
                    binding.tvMembuat.text = membuat

                    // Display the array data
                    val bahanText = bahanList.joinToString("\n") { "• $it" }
                    binding.tvBahan.text = bahanText
                }
            }
            .addOnFailureListener { exception ->
                // Handle any errors here
            }
    }
}