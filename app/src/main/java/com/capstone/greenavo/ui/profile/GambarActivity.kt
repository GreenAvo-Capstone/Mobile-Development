package com.capstone.greenavo.ui.profile

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.capstone.greenavo.R
import com.capstone.greenavo.databinding.ActivityGambarBinding
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FirebaseFirestore

class GambarActivity : AppCompatActivity() {
    private var _binding: ActivityGambarBinding? = null
    private val binding get() = _binding!!

    // Firestore instance
    private lateinit var db: FirebaseFirestore
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityGambarBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.ivBack.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        // Initialize Firestore
        db = FirebaseFirestore.getInstance()
        auth = Firebase.auth

        loadImage()
    }

    private fun loadImage() {
        val currentUser = auth.currentUser
        val userId = currentUser?.uid

        if (userId != null) {
            db.collection("users")
                .document(userId)
                .get()
                .addOnSuccessListener { document ->
                    if (document != null) {
                        //Gambar
                        val fotoProfil = document.getString("foto_profil")
                        if (fotoProfil != null) {
                            Glide.with(this)
                                .load(fotoProfil)
                                .into(binding.ivImageProfile)
                        } else {
                            binding.ivImageProfile.setImageResource(R.drawable.logo_full_apk)
                        }
                    } else {
                        Toast.makeText(this, "Data tidak ditemukan", Toast.LENGTH_SHORT).show()
                    }
                }
                .addOnFailureListener { exception ->
                    // Gagal mendapatkan data, tampilkan pesan error atau lakukan tindakan lain
                    Toast.makeText(this, "Error: ${exception.message}", Toast.LENGTH_SHORT).show()
                }
        } else {
            Toast.makeText(this, "Pengguna tidak ditemukan", Toast.LENGTH_SHORT).show()
        }
    }


    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}