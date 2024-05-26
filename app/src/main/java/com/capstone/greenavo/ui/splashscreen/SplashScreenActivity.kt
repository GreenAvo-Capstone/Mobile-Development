package com.capstone.greenavo.ui.splashscreen

import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.WindowInsets
import android.view.WindowManager
import android.view.animation.AnimationUtils
import android.widget.ImageView
import com.capstone.greenavo.R
import com.capstone.greenavo.databinding.ActivitySplashScreenBinding
import com.capstone.greenavo.ui.main.MainActivity

class SplashScreenActivity : AppCompatActivity() {
    private var _binding: ActivitySplashScreenBinding? = null
    private val binding get() = _binding!!

    private val SPLASH_SCREEN_DELAY = 3000L

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivitySplashScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupView()
    }

    private fun setupView() {
        @Suppress("DEPRECATION")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.insetsController?.hide(WindowInsets.Type.statusBars())
        } else {
            window.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
            )
        }
        supportActionBar?.hide()

        Handler(Looper.getMainLooper()).postDelayed({
            startActivity(Intent(this@SplashScreenActivity, MainActivity::class.java))
            finish()
        }, SPLASH_SCREEN_DELAY)

        val iconIMG = findViewById<ImageView>(R.id.iv_logo_splash_screen)
        val scaleAnimation = AnimationUtils.loadAnimation(this@SplashScreenActivity, R.anim.fade_in)
        iconIMG.startAnimation(scaleAnimation)
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}