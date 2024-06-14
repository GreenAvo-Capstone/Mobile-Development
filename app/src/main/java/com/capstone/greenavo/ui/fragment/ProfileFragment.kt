package com.capstone.greenavo.ui.fragment

import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.credentials.ClearCredentialStateRequest
import androidx.credentials.CredentialManager
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.capstone.greenavo.R
import com.capstone.greenavo.databinding.FragmentProfileBinding
import com.capstone.greenavo.databinding.LayoutKonfirmasiBinding
import com.capstone.greenavo.ui.profile.EditProfileActivity
import com.capstone.greenavo.ui.authetication.LoginActivity
import com.capstone.greenavo.ui.profile.GambarActivity
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.launch

class ProfileFragment : Fragment() {
    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!

    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore

    private var loadingDialog: AlertDialog? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnLogout.setOnClickListener {
            showPopupKonfirmasi()
        }
        binding.btnEditProfil.setOnClickListener {
            val intent = Intent(requireContext(), EditProfileActivity::class.java)
            startActivity(intent)
        }

        auth = Firebase.auth
        db = FirebaseFirestore.getInstance()

        binding.ivImageProfile.setOnClickListener{
            val intent = Intent(requireContext(), GambarActivity::class.java)
            startActivity(intent)
        }

        refresh()

        //Menampilkan semua data profil
        viewData()
    }

    private fun refresh() {
        binding.swipeRefreshLayout.setOnRefreshListener {
            binding.swipeRefreshLayout.isRefreshing = false
            viewData()
        }
    }

    private fun viewData() {
        val currentUser = auth.currentUser
        val userId = currentUser?.uid

        if (userId != null) {
            db.collection("users")
                .document(userId)
                .get()
                .addOnSuccessListener { document ->
                    if (document != null) {
                        val nama = document.getString("name")
                        val email = document.getString("email")
                        val nomortlp = document.getString("no_tlp")
                        val alamat = document.getString("alamat")

                        binding.tvNamaProfile.text = nama
                        binding.tvNamaEmail.text = email
                        binding.tvNomorTelepon.text = nomortlp
                        binding.tvAlamat.text = alamat

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
                        // Data tidak ditemukan
                        binding.tvNamaProfile.text = ""
                        binding.tvNamaEmail.text = ""
                        binding.tvNomorTelepon.text = ""
                        binding.tvAlamat.text = ""
                    }
                }
                .addOnFailureListener { exception ->
                    // Gagal mendapatkan data, tampilkan pesan error atau lakukan tindakan lain
                    Toast.makeText(activity, "Error: ${exception.message}", Toast.LENGTH_SHORT).show()
                }
        } else {
            Toast.makeText(activity, "Pengguna tidak ditemukan", Toast.LENGTH_SHORT).show()
        }
    }

    //Popup Informasi
    private fun showPopupKonfirmasi() {
        val dialogKonfirmasiBinding = LayoutKonfirmasiBinding.inflate(layoutInflater)
        dialogKonfirmasiBinding.tvKonfirmasi.text = "Apakah anda ingin keluar?"

        val dialog = AlertDialog.Builder(requireContext())
            .setView(dialogKonfirmasiBinding.root)
            .setCancelable(false)
            .create()

        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        dialogKonfirmasiBinding.btnYes.setOnClickListener {
            logout()
            dialog.dismiss()
        }

        dialogKonfirmasiBinding.btnNo.setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()
    }

    //Loading
    private fun showLoading() {
        if (loadingDialog == null) {
            val inflater = LayoutInflater.from(requireContext())
            val loadingView = inflater.inflate(R.layout.layout_loading, null)

            loadingDialog = AlertDialog.Builder(requireContext())
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

    //Logout
    private fun logout() {
        showLoading()
        lifecycleScope.launch {
            try {
                val credentialManager = CredentialManager.create(requireContext())
                auth.signOut()
                credentialManager.clearCredentialState(ClearCredentialStateRequest())

                hideLoading()

                startActivity(Intent(requireContext(), LoginActivity::class.java))
                requireActivity().finish()
            } catch (e: Exception){
                Toast.makeText(requireContext(), e.message, Toast.LENGTH_SHORT).show()
                hideLoading()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}