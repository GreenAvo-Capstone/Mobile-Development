package com.capstone.greenavo.ui.authetication

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.ContentValues
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import com.capstone.greenavo.databinding.ActivityRegisterBinding
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FirebaseFirestore

class RegisterActivity : AppCompatActivity() {
    private var _binding: ActivityRegisterBinding? = null
    private val binding get() = _binding!!

    //Firebase auth
    private lateinit var auth: FirebaseAuth
    //Firebase firestore
    private lateinit var db: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.ivBack.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        binding.tvLogin.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
        }

        auth = Firebase.auth
        db = FirebaseFirestore.getInstance()


        actionRegister()
        //Animasi
        playAnimation()
    }

    private fun playAnimation() {
        val back = ObjectAnimator.ofFloat(binding.ivBack, View.ALPHA, 1f).setDuration(100)
        val title = ObjectAnimator.ofFloat(binding.tvHeaderRegister, View.ALPHA, 1f).setDuration(100)
        val deskripsiRegister =
            ObjectAnimator.ofFloat(binding.tvDeskripsiRegister, View.ALPHA, 1f).setDuration(100)
        val nameTextView =
            ObjectAnimator.ofFloat(binding.tvNama, View.ALPHA, 1f).setDuration(100)
        val namaEditTextLayout =
            ObjectAnimator.ofFloat(binding.etNamaLayout, View.ALPHA, 1f).setDuration(100)
        val emailTextView =
            ObjectAnimator.ofFloat(binding.tvEmail, View.ALPHA, 1f).setDuration(100)
        val emailEditTextLayout =
            ObjectAnimator.ofFloat(binding.etEmailLayout, View.ALPHA, 1f).setDuration(100)
        val passwordTextView =
            ObjectAnimator.ofFloat(binding.tvPassword, View.ALPHA, 1f).setDuration(100)
        val passwordEditTextLayout =
            ObjectAnimator.ofFloat(binding.etPasswordLayout, View.ALPHA, 1f).setDuration(100)
        val register = ObjectAnimator.ofFloat(binding.btnRegister, View.ALPHA, 1f).setDuration(100)
        val deskripsiLogin = ObjectAnimator.ofFloat(binding.tvDeskripsiLogin, View.ALPHA, 1f).setDuration(100)
        val login = ObjectAnimator.ofFloat(binding.tvLogin, View.ALPHA, 1F).setDuration(100)

        AnimatorSet().apply {
            playSequentially(
                back,
                title,
                deskripsiRegister,
                nameTextView,
                namaEditTextLayout,
                emailTextView,
                emailEditTextLayout,
                passwordTextView,
                passwordEditTextLayout,
                register,
                deskripsiLogin,
                login
            )
            startDelay = 100
        }.start()
    }

    private fun actionRegister() {
        binding.btnRegister.setOnClickListener {
            val name = binding.etNama.text.toString()
            val email = binding.etEmail.text.toString()
            val password = binding.etPassword.text.toString()
            if (name.isNotEmpty() && email.isNotEmpty() && password.isNotEmpty()) {
                auth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this) { task ->
                        if (task.isSuccessful) {
                            val user = auth.currentUser
                            assert(user != null)
                            val userId = user!!.uid

                            val userMap = hashMapOf(
                                "name" to name,
                                "email" to email,
                                "password" to password
                            )

                            db.collection("users").document(userId).set(userMap)
                                .addOnSuccessListener { documentReference ->
                                    Log.d(ContentValues.TAG, "DocumentSnapshot added with ID: $userId")
                                }
                                .addOnFailureListener { e ->
                                    Log.w(ContentValues.TAG, "Error adding document", e)
                                }

                            // Berhasil masuk, perbarui UI dengan informasi pengguna yang masuk
                            Log.d(ContentValues.TAG, "createUserWithEmail:success")
                            Toast.makeText(this, "Pendaftaran berhasil!", Toast.LENGTH_SHORT).show()
                            updateUI(user)
                        } else {
                            Toast.makeText(this, "Pendaftaran gagal!", Toast.LENGTH_SHORT).show()
                            updateUI(null)
                        }
                    }
            } else {
                Toast.makeText(this, "Lengkapi data!", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun updateUI(user: FirebaseUser?) {
        if (user != null) {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }
    }

    override fun onStart() {
        super.onStart()
        val currentUser = auth.currentUser
        updateUI(currentUser)
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}