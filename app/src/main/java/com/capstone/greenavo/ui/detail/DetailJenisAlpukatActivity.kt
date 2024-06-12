package com.capstone.greenavo.ui.detail

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.bumptech.glide.Glide
import com.capstone.greenavo.databinding.ActivityDetailJenisAlpukatBinding
import com.google.firebase.firestore.FirebaseFirestore

class DetailJenisAlpukatActivity : AppCompatActivity() {
    private var _binding: ActivityDetailJenisAlpukatBinding? = null
    private val binding get() = _binding!!

    private lateinit var db: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityDetailJenisAlpukatBinding.inflate(layoutInflater)
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
        db.collection("jenis_alpukat").document(documentId)
            .get()
            .addOnSuccessListener { document ->
                if (document != null && document.exists()) {
                    // Retrieve the data from the document
                    val nama = document.getString("nama") ?: ""
                    val gambar = document.getString("gambar") ?: ""
                    val deskripsi = document.getString("deskripsi") ?: ""
                    val sumberLemak = document.getString("sumber_lemak") ?: ""
                    val tinggiSerat = document.getString("tinggi_serat") ?: ""
                    val kayaNutrisi = document.getString("kaya_nutrisi") ?: ""
                    val antioksidan = document.getString("antioksidan") ?: ""
                    val manfaatKulit = document.getString("manfaat_kulit") ?: ""

                    // Populate the views with the data
                    binding.tvNama.text = nama
                    Glide.with(this).load(gambar).into(binding.ivGambar)
                    binding.tvDeskripsi.text = deskripsi
                    binding.tvSumberLemakSehatSerat.text = sumberLemak
                    binding.tvTinggiSerat.text = tinggiSerat
                    binding.tvKayaAkanNutrisi.text = kayaNutrisi
                    binding.tvAntioksidan.text = antioksidan
                    binding.tvManfaatKulit.text = manfaatKulit
                }
            }
            .addOnFailureListener { exception ->
                // Handle any errors here
            }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}