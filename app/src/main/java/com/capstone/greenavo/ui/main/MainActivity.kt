package com.capstone.greenavo.ui.main

import android.content.Intent
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.Fragment
import com.capstone.greenavo.R
import com.capstone.greenavo.databinding.ActivityMainBinding
import com.capstone.greenavo.ui.ViewModelFactory
import com.capstone.greenavo.ui.camera.CameraFragment
import com.capstone.greenavo.ui.history.HistoryActivity
import com.capstone.greenavo.ui.home.HomeFragment
import com.capstone.greenavo.ui.login.LoginActivity
import com.capstone.greenavo.ui.profile.ProfileFragment


class MainActivity : AppCompatActivity() {
    private var _binding:ActivityMainBinding? = null
    private val binding get() = _binding!!

    //Select fragment Home
    private var selectedItemId: Int = R.id.home

    //View Model
    private val viewModel by viewModels<MainViewModel> {
        ViewModelFactory.getInstance(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)

        if (savedInstanceState != null) {
            selectedItemId = savedInstanceState.getInt(KEY_SELECTED_ITEM, R.id.home)
        }

        setupBottomNavigationView()

        when (selectedItemId) {
            R.id.home -> replaceFragment(HomeFragment())
            R.id.camera -> replaceFragment(CameraFragment())
            R.id.profile -> replaceFragment(ProfileFragment())
        }

        binding.ivHistory.setOnClickListener {
            val intent = Intent(this, HistoryActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
            startActivity(intent)
        }

        viewModel.getSession().observe(this) { user ->
            if (user.token == "") {
                startActivity(Intent(this, LoginActivity::class.java))
                finish()
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
    }

    fun setBottomNavigationEnabled(aktif: Boolean) {
        binding.bottomNavigationView.menu.findItem(R.id.home)?.isEnabled = aktif
        binding.bottomNavigationView.menu.findItem(R.id.camera)?.isEnabled = aktif
        binding.bottomNavigationView.menu.findItem(R.id.profile)?.isEnabled = aktif
        binding.ivHistory.isEnabled = aktif
    }

    private fun setupBottomNavigationView() {
        binding.bottomNavigationView.setOnItemSelectedListener {
            selectedItemId = it.itemId

            when (it.itemId) {
                R.id.home -> replaceFragment(HomeFragment())
                R.id.camera -> replaceFragment(CameraFragment())
                R.id.profile -> replaceFragment(ProfileFragment())
                else -> {
                    Toast.makeText(this, "Coming soon", Toast.LENGTH_SHORT).show()
                }
            }
            true
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putInt(KEY_SELECTED_ITEM, selectedItemId)
    }

    private fun replaceFragment(fragment: Fragment) {
        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.frame_layout, fragment)
        fragmentTransaction.commit()
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    companion object {
        private const val KEY_SELECTED_ITEM = "selected_item"
    }
}