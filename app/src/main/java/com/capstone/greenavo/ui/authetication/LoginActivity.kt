package com.capstone.greenavo.ui.authetication

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import androidx.appcompat.app.AlertDialog
import com.capstone.greenavo.R
import com.capstone.greenavo.databinding.ActivityLoginBinding
import com.capstone.greenavo.databinding.LayoutFailedBinding
import com.capstone.greenavo.databinding.LayoutPeringatanBinding
import com.capstone.greenavo.databinding.LayoutSuccessBinding
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

    private var loadingDialog: AlertDialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = Firebase.auth

        binding.btnLogin.setOnClickListener{
            actionLogin()
        }

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
        val email = binding.etEmail.text.toString()
        val password = binding.etPassword.text.toString()

        if (email.isNotEmpty() && password.isNotEmpty() ) {

            showLoading()

            auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this) { task ->

                    hideLoading()

                    if (task.isSuccessful) {
                        showPopupBerhasil()
                    } else {
                        showPopupGagal()
                    }
                }
        } else {
            showPopupPeringatan()
        }
    }

    //Popup berhasil
    private fun showPopupBerhasil() {
        val dialogSuccessBinding = LayoutSuccessBinding.inflate(layoutInflater)

        dialogSuccessBinding.tvSuccess.text = "Berhasil login!!"

        val dialog = AlertDialog.Builder(this@LoginActivity)
            .setView(dialogSuccessBinding.root)
            .setCancelable(false)
            .create()

        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        dialogSuccessBinding.btnOk.setOnClickListener {
            dialog.dismiss()
            val user = auth.currentUser
            updateUi(user)
        }

        dialog.show()
    }

    //Popup gagal
    private fun showPopupGagal() {
        val dialogSuccessBinding = LayoutFailedBinding.inflate(layoutInflater)

        dialogSuccessBinding.tvFailed.text = "Gagal login!!"

        val dialog = AlertDialog.Builder(this@LoginActivity)
            .setView(dialogSuccessBinding.root)
            .setCancelable(false)
            .create()

        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        dialogSuccessBinding.btnOk.setOnClickListener {
            dialog.dismiss()
            updateUi(null)
        }

        dialog.show()
    }

    //Popup peringatan
    private fun showPopupPeringatan() {
        val dialogPeringatanBinding = LayoutPeringatanBinding.inflate(layoutInflater)
        dialogPeringatanBinding.tvPeringatan.text = "Harap isi semua field"

        val dialog = AlertDialog.Builder(this@LoginActivity)
            .setView(dialogPeringatanBinding.root)
            .setCancelable(false)
            .create()

        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        dialogPeringatanBinding.btnYes.setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()
    }

    //Loading
    private fun showLoading() {
        if (loadingDialog == null) {
            val inflater = LayoutInflater.from(this@LoginActivity)
            val loadingView = inflater.inflate(R.layout.layout_loading, null)

            loadingDialog = AlertDialog.Builder(this@LoginActivity)
                .setView(loadingView)
                .setCancelable(false)
                .create()

            loadingDialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        }

        loadingDialog?.show()
    }

    private fun hideLoading() {
        loadingDialog?.dismiss()
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