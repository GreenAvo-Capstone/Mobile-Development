package com.capstone.greenavo.ui

import android.content.Intent
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.Fragment
import com.capstone.greenavo.R
import com.capstone.greenavo.databinding.ActivityMainBinding
import com.capstone.greenavo.ui.authetication.LoginActivity
import com.capstone.greenavo.ui.fragment.CameraFragment
import com.capstone.greenavo.ui.fragment.HomeFragment
import com.capstone.greenavo.ui.fragment.ProfileFragment
import com.capstone.greenavo.ui.rekomendasi.FavoriteActivity
import com.capstone.greenavo.ui.result.HistoryActivity
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth

class MainActivity : AppCompatActivity() {
    private var _binding:ActivityMainBinding? = null
    private val binding get() = _binding!!

    //Select fragment Home
    private var selectedItemId: Int = R.id.home

    //Firebase
    private lateinit var auth: FirebaseAuth

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
            startActivity(intent)
        }

        binding.ivFavorite.setOnClickListener {
            val intent = Intent(this, FavoriteActivity::class.java)
            startActivity(intent)
        }

        auth = Firebase.auth
        val firebasetUser = auth.currentUser
        if (firebasetUser == null){
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
            return
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