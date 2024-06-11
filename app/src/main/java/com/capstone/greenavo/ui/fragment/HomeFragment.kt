package com.capstone.greenavo.ui.fragment

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.capstone.greenavo.R
import com.capstone.greenavo.data.JenisAlpukat
import com.capstone.greenavo.data.ResepAlpukat
import com.capstone.greenavo.databinding.FragmentHomeBinding
import com.capstone.greenavo.adapter.JenisAlpukatAdapter
import com.capstone.greenavo.adapter.ResepAlpukatAdapter
import com.google.firebase.Firebase
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore
import com.google.firebase.firestore.toObject

class HomeFragment : Fragment() {
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    //Adapter
    private lateinit var jenisAlpukatAdapter: JenisAlpukatAdapter
    private lateinit var resepAlpukatAdapter: ResepAlpukatAdapter

    //List
    private val jenisAlpukatList: MutableList<JenisAlpukat> = mutableListOf()
    private val resepAlpukatList: MutableList<ResepAlpukat> = mutableListOf()

    //Firestore
    private lateinit var db: FirebaseFirestore

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // Initialize Firebase
        db = Firebase.firestore

        // Initialize the RecyclerView
        binding.rvJenisAlpukat.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        binding.rvResepAlpukat.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)

        // Call the function to fetch data from Firestore
        showJenisAlpukat()
        showResepAlpukat()
    }

    private fun showJenisAlpukat() {
        db.collection("jenis_alpukat")
            .get()
            .addOnSuccessListener { documentAlpukat ->
                for (document in documentAlpukat) {
                    val namaAlpukat = document.getString("nama_alpukat") ?:""
                    val gambarAlpukat = document.getString("gambar")?:""
                    jenisAlpukatList.add(JenisAlpukat(namaAlpukat, gambarAlpukat))
                }

                jenisAlpukatAdapter = JenisAlpukatAdapter(jenisAlpukatList)
                binding.rvJenisAlpukat.adapter = jenisAlpukatAdapter
            }
            .addOnFailureListener { exception ->
                Toast.makeText(requireContext(), exception.message, Toast.LENGTH_SHORT).show()
            }
    }

    private fun showResepAlpukat() {
        db.collection("resep_alpukat")
            .get()
            .addOnSuccessListener { documentResep ->
                for (document in documentResep) {
                    val namaResep = document.getString("nama_resep") ?: ""
                    val gambarResep = document.getString("gambar") ?: ""
                    resepAlpukatList.add(ResepAlpukat(namaResep, gambarResep))
                }
                resepAlpukatAdapter = ResepAlpukatAdapter(resepAlpukatList)
                binding.rvResepAlpukat.adapter = resepAlpukatAdapter
            }
            .addOnFailureListener { exception ->
                Toast.makeText(requireContext(), exception.message, Toast.LENGTH_SHORT).show()
            }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}