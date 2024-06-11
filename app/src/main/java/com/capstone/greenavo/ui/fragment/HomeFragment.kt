package com.capstone.greenavo.ui.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.capstone.greenavo.R
import com.capstone.greenavo.data.JenisAlpukat
import com.capstone.greenavo.data.ResepAlpukat
import com.capstone.greenavo.databinding.FragmentHomeBinding
import com.capstone.greenavo.adapter.JenisAlpukatAdapter
import com.capstone.greenavo.adapter.ResepAlpukatAdapter

class HomeFragment : Fragment() {
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private lateinit var jenisAlpukatAdapter: JenisAlpukatAdapter
    private lateinit var resepAlpukatAdapter: ResepAlpukatAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        showJenisAlpukat()
        showResepAlpukat()
    }

    //Resep Alpukat (Percobaan)
    private fun showResepAlpukat() {
        val namaResepAlpukatArray = resources.getStringArray(R.array.nama_resep)
        val gambarResepAlpukatArray = resources.getStringArray(R.array.gambar_resep)

        val resepAlpukatList = namaResepAlpukatArray.zip(gambarResepAlpukatArray) { nama, gambar ->
            ResepAlpukat(nama, gambar)
        }

        resepAlpukatAdapter = ResepAlpukatAdapter(resepAlpukatList)
        binding.rvResepAlpukat.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        binding.rvResepAlpukat.adapter = resepAlpukatAdapter
    }

    //jenis Alpukat (Percobaan)
    private fun showJenisAlpukat() {
        val namaAlpukatArray = resources.getStringArray(R.array.nama_alpukat)
        val gambarAlpukatArray = resources.getStringArray(R.array.gambar_alpukat)
        val deskripsiSatuAlpukatArray = resources.getStringArray(R.array.deskripsi_satu)
        val deskripsiDuaAlpukatArray = resources.getStringArray(R.array.deskripsi_dua)
        val sumberLemakSehatSeratArray = resources.getStringArray(R.array.sumber_lemak_sehat_serat)
        val kayaAkanNutrisiArray = resources.getStringArray(R.array.kaya_akan_nutrisi)
        val tinggiSeratArray = resources.getStringArray(R.array.tinggi_serat)
        val antioksidanArray = resources.getStringArray(R.array.antioksidan)
        val manfaatKulitArray = resources.getStringArray(R.array.manfaat_kulit)

        val jenisAlpukatList = namaAlpukatArray.indices.map { index ->
            JenisAlpukat(
                namaAlpukat = namaAlpukatArray[index],
                gambarAlpukat = gambarAlpukatArray[index],
                deskripsiSatu = deskripsiSatuAlpukatArray[index],
                deskripsiDua = deskripsiDuaAlpukatArray[index],
                sumberLemakSehatSerat = sumberLemakSehatSeratArray[index],
                kayaAkanNutrisi = kayaAkanNutrisiArray[index],
                tinggiSerat = tinggiSeratArray[index],
                antioksidan = antioksidanArray[index],
                manfaatKulit = manfaatKulitArray[index]
            )
        }

        jenisAlpukatAdapter = JenisAlpukatAdapter(jenisAlpukatList)
        binding.rvJenisAlpukat.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        binding.rvJenisAlpukat.adapter = jenisAlpukatAdapter
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}