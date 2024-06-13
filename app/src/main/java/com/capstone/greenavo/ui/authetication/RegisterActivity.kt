package com.capstone.greenavo.ui.authetication

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.ContentValues
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.capstone.greenavo.R
import com.capstone.greenavo.databinding.ActivityRegisterBinding
import com.capstone.greenavo.databinding.LayoutFailedBinding
import com.capstone.greenavo.databinding.LayoutPeringatanBinding
import com.capstone.greenavo.databinding.LayoutSuccessBinding
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

    private var loadingDialog: AlertDialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.ivBack.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        binding.tvLogin.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        auth = Firebase.auth
        db = FirebaseFirestore.getInstance()


        binding.btnRegister.setOnClickListener{
            actionRegister()
        }

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
        val name = binding.etNama.text.toString()
        val email = binding.etEmail.text.toString()
        val password = binding.etPassword.text.toString()
        if (name.isNotEmpty() && email.isNotEmpty() && password.isNotEmpty()) {

            showLoading()

            auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this) { task ->

                    hideLoading()

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
                                Toast.makeText(this, "Pendaftaran berhasil!", Toast.LENGTH_SHORT).show()
                            }
                            .addOnFailureListener {
                                showPopupGagal("Pendaftaran gagal!")
                            }

                        // Berhasil masuk, perbarui UI dengan informasi pengguna yang masuk
                        Log.d(ContentValues.TAG, "createUserWithEmail:success")
                        updateUI(user)
                    } else {
                        showPopupGagal("Pendaftaran gagal!")
                    }
                }
        } else {
            showPopupPeringatan("Lengkapi Data Terlebih Dahulu")
        }
    }

    //Popup gagal
    private fun showPopupGagal(message: String) {
        val dialogSuccessBinding = LayoutFailedBinding.inflate(layoutInflater)

        dialogSuccessBinding.tvFailed.text = message

        val dialog = AlertDialog.Builder(this@RegisterActivity)
            .setView(dialogSuccessBinding.root)
            .setCancelable(false)
            .create()

        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        dialogSuccessBinding.btnOk.setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()
    }

    //Popup peringatan
    private fun showPopupPeringatan(message: String) {
        val dialogPeringatanBinding = LayoutPeringatanBinding.inflate(layoutInflater)
        dialogPeringatanBinding.tvPeringatan.text = message

        val dialog = AlertDialog.Builder(this@RegisterActivity)
            .setView(dialogPeringatanBinding.root)
            .setCancelable(false)
            .create()

        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        dialogPeringatanBinding.btnYes.setOnClickListener {
            dialog.dismiss()
            updateUI(null)
        }

        dialog.show()
    }

    //Loading
    private fun showLoading() {
        if (loadingDialog == null) {
            val inflater = LayoutInflater.from(this@RegisterActivity)
            val loadingView = inflater.inflate(R.layout.layout_loading, null)

            loadingDialog = AlertDialog.Builder(this@RegisterActivity)
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