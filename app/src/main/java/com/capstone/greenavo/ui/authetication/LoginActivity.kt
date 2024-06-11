package com.capstone.greenavo.ui.authetication

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.app.Dialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.capstone.greenavo.databinding.ActivityLoginBinding
import com.capstone.greenavo.ui.MainActivity
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.auth

class LoginActivity : AppCompatActivity() {
    private var _binding: ActivityLoginBinding? = null
    private val binding get() = _binding!!

    //Firebase Auth
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = Firebase.auth

        actionLogin()

        //Animasi
        playAnimation()

        binding.tvRegister.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }
    }

    private fun playAnimation() {
        val title = ObjectAnimator.ofFloat(binding.tvHeaderLogin, View.ALPHA, 1f).setDuration(100)
        val deskripsiLogin =
            ObjectAnimator.ofFloat(binding.tvDeskripsiLogin, View.ALPHA, 1f).setDuration(100)
        val emailTextView =
            ObjectAnimator.ofFloat(binding.tvEmail, View.ALPHA, 1f).setDuration(100)
        val emailEditTextLayout =
            ObjectAnimator.ofFloat(binding.etEmailLayout, View.ALPHA, 1f).setDuration(100)
        val passwordTextView =
            ObjectAnimator.ofFloat(binding.tvPassword, View.ALPHA, 1f).setDuration(100)
        val passwordEditTextLayout =
            ObjectAnimator.ofFloat(binding.etPasswordLayout, View.ALPHA, 1f).setDuration(100)
        val login = ObjectAnimator.ofFloat(binding.btnLogin, View.ALPHA, 1f).setDuration(100)
        val deskripsiRegister = ObjectAnimator.ofFloat(binding.tvDeskripsiRegister, View.ALPHA, 1f).setDuration(100)
        val register = ObjectAnimator.ofFloat(binding.tvRegister, View.ALPHA, 1F).setDuration(100)

        AnimatorSet().apply {
            playSequentially(
                title,
                deskripsiLogin,
                emailTextView,
                emailEditTextLayout,
                passwordTextView,
                passwordEditTextLayout,
                login,
                deskripsiRegister,
                register
            )
            startDelay = 100
        }.start()
    }

    //Fungsi menekan tombol login
    private fun actionLogin() {
        binding.btnLogin.setOnClickListener {
            val email = binding.etEmail.text.toString()
            val password = binding.etPassword.text.toString()

            if (email.isNotEmpty() && password.isNotEmpty() ) {
                auth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this) { task ->
                        if (task.isSuccessful) {
                            val user = auth.currentUser
                            Toast.makeText(this, "Login berhasil", Toast.LENGTH_SHORT).show()
                            updateUi(user)
                        } else {
                            Toast.makeText(this, "Login gagal", Toast.LENGTH_SHORT).show()
                            updateUi(null)
                        }
                    }
            } else {
                Toast.makeText(this, "Harap isi semua field", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun updateUi(user: FirebaseUser?) {
        if (user != null) {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }
    }

    override fun onStart() {
        super.onStart()
        val currentUser = auth.currentUser
        updateUi(currentUser)
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}