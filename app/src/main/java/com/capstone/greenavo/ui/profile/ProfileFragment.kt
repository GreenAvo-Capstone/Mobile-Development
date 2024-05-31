package com.capstone.greenavo.ui.profile

import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.viewModels
import com.capstone.greenavo.databinding.FragmentProfileBinding
import com.capstone.greenavo.databinding.LayoutKonfirmasiBinding
import com.capstone.greenavo.databinding.LayoutLoadingBinding
import com.capstone.greenavo.ui.ViewModelFactory
import com.capstone.greenavo.ui.editprofile.EditProfileActivity
import com.capstone.greenavo.ui.main.MainViewModel

class ProfileFragment : Fragment() {
    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!

    private val viewModel by viewModels<ProfileViewModel> {
        ViewModelFactory.getInstance(requireActivity())
    }

    //Loading
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
            showPoupKonfirmasi()
        }
        binding.btnEditProfil.setOnClickListener {
            val intent = Intent(requireContext(), EditProfileActivity::class.java)
            startActivity(intent)
        }
    }

    private fun showPoupKonfirmasi() {
        val dialogKonfirmasiBinding = LayoutKonfirmasiBinding.inflate(layoutInflater)

        dialogKonfirmasiBinding.tvKonfirmasi.text = "Apakah ingin anda keluar?"

        val dialog = AlertDialog.Builder(requireContext())
            .setView(dialogKonfirmasiBinding.root)
            .setCancelable(false)
            .create()
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        dialogKonfirmasiBinding.btnYes.setOnClickListener {
            viewModel.logout()
            showLoading()
            dialog.dismiss()
        }

        dialogKonfirmasiBinding.btnNo.setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()
    }

    private fun showLoading() {
        val loadingBinding = LayoutLoadingBinding.inflate(layoutInflater)
        loadingDialog = AlertDialog.Builder(requireContext())
            .setView(loadingBinding.root)
            .setCancelable(false)
            .create()
        loadingDialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        loadingDialog?.show()
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}