package com.capstone.greenavo.ui.register

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import com.capstone.greenavo.R
import com.capstone.greenavo.data.ResultState
import com.capstone.greenavo.databinding.ActivityRegisterBinding
import com.capstone.greenavo.databinding.LayoutFailedBinding
import com.capstone.greenavo.databinding.LayoutSuccessBinding
import com.capstone.greenavo.ui.ViewModelFactory
import com.capstone.greenavo.ui.login.LoginActivity
import com.capstone.greenavo.ui.login.LoginViewModel
import com.capstone.greenavo.ui.main.MainActivity

class RegisterActivity : AppCompatActivity() {
    private var _binding: ActivityRegisterBinding? = null
    private val binding get() = _binding!!

    //Loading
    private var loadingDialog : Dialog? = null

    //View Model
    private val viewModel by viewModels<RegisterViewModel> {
        ViewModelFactory.getInstance(this)
    }

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

        setupAction()
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

    private fun buttonClicklable(isTrue : Boolean){
        binding.btnRegister.isEnabled = isTrue
        binding.etNama.isEnabled = isTrue
        binding.etEmail.isEnabled = isTrue
        binding.etPassword.isEnabled = isTrue
    }

    private fun setupAction() {
        binding.btnRegister.setOnClickListener {
            //Fungsi tombol register
            val nama = binding.etNama.text.toString()
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

            viewModel.register(nama, email, password).observe(this) { result ->
                when(result){
                    is ResultState.Loading -> {
                        buttonClicklable(false)
                        showLoading(true)
                    }
                    is ResultState.Success -> {
                        buttonClicklable(true)
                        showLoading(false)
                        val registerResponse = result.data
                        showPopupBerhasil("${registerResponse.message}", true)
                    }
                    is ResultState.Error -> {
                        showLoading(false)
                        buttonClicklable(true)
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
                finish()
            }
            dialog.dismiss()
        }

        dialog.show()
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
                intentToLogin()
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

    private fun intentToLogin(){
        startActivity(Intent(this, LoginActivity::class.java))
        finish()
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