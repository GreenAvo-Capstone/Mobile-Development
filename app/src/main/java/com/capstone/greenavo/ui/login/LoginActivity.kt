package com.capstone.greenavo.ui.login

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import com.capstone.greenavo.R
import com.capstone.greenavo.data.ResultState
import com.capstone.greenavo.databinding.ActivityLoginBinding
import com.capstone.greenavo.databinding.LayoutFailedBinding
import com.capstone.greenavo.databinding.LayoutSuccessBinding
import com.capstone.greenavo.ui.ViewModelFactory
import com.capstone.greenavo.ui.main.MainActivity
import com.capstone.greenavo.ui.register.RegisterActivity

class LoginActivity : AppCompatActivity() {
    private var _binding: ActivityLoginBinding? = null
    private val binding get() = _binding!!

    //Loading
    private var loadingDialog : Dialog? = null

    //View Model
    private val viewModel by viewModels<LoginViewModel> {
        ViewModelFactory.getInstance(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.tvRegister.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }

        setupAction()
        playAnimation()

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

    private fun buttonClicklable(isTrue: Boolean){
        binding.btnLogin.isClickable = isTrue
        binding.etEmail.isEnabled = isTrue
        binding.etPassword.isEnabled = isTrue
    }

    private fun setupAction() {
        binding.btnLogin.setOnClickListener {
            //Fungsi tombol login
            val email = binding.etEmail.text.toString()
            val password = binding.etPassword.text.toString()

            if (!isEmailValid(email)) {
                binding.etEmail.error = getString(R.string.invalid_email_format)
                return@setOnClickListener
            }

            if (password.length < 8) {
                binding.etPassword.error = getString(R.string.length_of_password)
                return@setOnClickListener
            }

            viewModel.login(email, password).observe(this) { result ->
                when(result){
                    is ResultState.Loading -> {
                        buttonClicklable(false)
                        showLoading(true)
                    }
                    is ResultState.Success -> {
                        buttonClicklable(true)
                        showLoading(false)
                        val loginResponse = result.data
                        loginResponse.loginResult?.let { user ->
                            viewModel.saveSession(user)
                        }
                        showPopupBerhasil("${loginResponse.message}", true)
                    }
                    is ResultState.Error -> {
                        buttonClicklable(true)
                        showLoading(false)
                        val errorMessage = result.message
                        showPopupGagal("$errorMessage", false)
                    }
                }
            }
        }
    }

    private fun showPopupBerhasil(message: String, status: Boolean) {
        val dialogSuccessBinding = LayoutSuccessBinding.inflate(layoutInflater)

        dialogSuccessBinding.tvSuccess.text = message

        val dialog = AlertDialog.Builder(this)
            .setView(dialogSuccessBinding.root)
            .setCancelable(false)
            .create()

        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        dialogSuccessBinding.btnOk.setOnClickListener {
            if (status){
                intentToMain()
                finish()
            }
            dialog.dismiss()
        }

        dialog.show()
    }

    private fun intentToMain() {
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }

    private fun showPopupGagal(message: String, status: Boolean) {
        val dialogFailedBinding = LayoutFailedBinding.inflate(layoutInflater)

        dialogFailedBinding.tvFailed.text = message

        val dialog = AlertDialog.Builder(this)
            .setView(dialogFailedBinding.root)
            .setCancelable(false)
            .create()

        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        dialogFailedBinding.btnOk.setOnClickListener {
            if (status){
                finish()
            }
            dialog.dismiss()
        }

        dialog.show()
    }

    private fun showLoading(isLoading: Boolean) {
        if (isLoading){
            if (loadingDialog == null){
                loadingDialog = Dialog(this).apply {
                    window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
                    setContentView(R.layout.layout_loading)
                    setCancelable(false)
                    setCanceledOnTouchOutside(false)
                }
            }
            loadingDialog?.show()
        } else {
            loadingDialog?.dismiss()
        }
    }

    private fun isEmailValid(email: String): Boolean {
        val emailPattern = "^[a-zA-Z0-9_]+@gmail\\.com\$"
        return email.matches(emailPattern.toRegex())
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}