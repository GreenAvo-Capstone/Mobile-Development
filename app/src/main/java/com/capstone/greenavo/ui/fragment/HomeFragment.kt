package com.capstone.greenavo.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.capstone.greenavo.adapter.JenisAlpukatAdapter
import com.capstone.greenavo.adapter.ResepAlpukatAdapter
import com.capstone.greenavo.data.JenisAlpukat
import com.capstone.greenavo.data.ResepAlpukat
import com.capstone.greenavo.databinding.FragmentHomeBinding
import com.google.firebase.firestore.FirebaseFirestore

class HomeFragment : Fragment() {
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    // Adapter
    private lateinit var jenisAlpukatAdapter: JenisAlpukatAdapter
    private lateinit var resepAlpukatAdapter: ResepAlpukatAdapter

    // Lists
    private val jenisAlpukatList: MutableList<JenisAlpukat> = mutableListOf()
    private val resepAlpukatList: MutableList<ResepAlpukat> = mutableListOf()

    // Firestore
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
        db = FirebaseFirestore.getInstance()

        // Initialize RecyclerViews
        binding.rvJenisAlpukat.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        binding.rvResepAlpukat.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)

        // Call functions to show data
        showJenisAlpukat()
        showResepAlpukat()
    }

    private fun showJenisAlpukat() {
        _binding?.apply {
            progressIndicatorAlpukat.visibility = View.VISIBLE
        }

        db.collection("jenis_alpukat")
            .get()
            .addOnSuccessListener { documents ->
                _binding?.apply {
                    jenisAlpukatList.clear()
                    for (document in documents) {
                        val nama = document.getString("nama") ?: ""
                        val gambar = document.getString("gambar") ?: ""
                        val deskripsi = document.getString("deskripsi") ?: ""
                        val sumberLemak = document.getString("sumber_lemak") ?: ""
                        val tinggiSerat = document.getString("tinggi_serat") ?: ""
                        val kayaNutrisi = document.getString("kaya_nutrisi") ?: ""
                        val antioksidan = document.getString("antioksidan") ?: ""
                        val manfaatKulit = document.getString("manfaat_kulit") ?: ""
                        val documentId = document.id  // Get the document ID

                        jenisAlpukatList.add(
                            JenisAlpukat(
                                documentId,
                                nama,
                                gambar,
                                deskripsi,
                                sumberLemak,
                                tinggiSerat,
                                kayaNutrisi,
                                antioksidan,
                                manfaatKulit
                            )
                        )
                    }
                    jenisAlpukatAdapter = JenisAlpukatAdapter(jenisAlpukatList)
                    rvJenisAlpukat.adapter = jenisAlpukatAdapter
                    progressIndicatorAlpukat.visibility = View.GONE
                }
            }
            .addOnFailureListener { exception ->
                _binding?.apply {
                    Toast.makeText(requireContext(), exception.message, Toast.LENGTH_SHORT).show()
                    progressIndicatorAlpukat.visibility = View.GONE
                }
            }
    }

    private fun showResepAlpukat() {
        _binding?.apply {
            progressIndicatorResep.visibility = View.VISIBLE
        }

        db.collection("resep_alpukat")
            .get()
            .addOnSuccessListener { documents ->
                _binding?.apply {
                    resepAlpukatList.clear()
                    for (document in documents) {
                        val nama = document.getString("nama_resep") ?: ""
                        val gambar = document.getString("gambar") ?: ""
                        val deskripsi = document.getString("deskripsi") ?: ""
                        val bahanArray = document.get("bahan") as? ArrayList<String> ?: arrayListOf()
                        val membuatArray = document.get("cara_membuat") as? ArrayList<String> ?: arrayListOf()
                        val documentId = document.id

                        // Convert array bahan menjadi string
                        val bahan = bahanArray.joinToString("\n") { "• $it" }
                        val membuat = membuatArray.joinToString("\n") { "$it" }

                        resepAlpukatList.add(
                            ResepAlpukat(
                                documentId,
                                nama,
                                gambar,
                                deskripsi,
                                bahan,
                                membuat
                            )
                        )
                    }
                    resepAlpukatAdapter = ResepAlpukatAdapter(resepAlpukatList)
                    rvResepAlpukat.adapter = resepAlpukatAdapter
                    progressIndicatorResep.visibility = View.GONE
                }
            }
            .addOnFailureListener { exception ->
                _binding?.apply {
                    Toast.makeText(requireContext(), exception.message, Toast.LENGTH_SHORT).show()
                    progressIndicatorResep.visibility = View.GONE
                }
            }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
