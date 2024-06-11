package com.capstone.greenavo.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.capstone.greenavo.data.JenisAlpukat
import com.capstone.greenavo.databinding.ItemAlpukatBinding

class JenisAlpukatAdapter(private val alpukatList: List<JenisAlpukat>): RecyclerView.Adapter<JenisAlpukatAdapter.AlpukatViewHolder>() {
    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): AlpukatViewHolder {
        val binding = ItemAlpukatBinding.inflate(LayoutInflater.from(viewGroup.context), viewGroup, false)
        return AlpukatViewHolder(binding)
    }

    inner class AlpukatViewHolder(val binding: ItemAlpukatBinding): RecyclerView.ViewHolder(binding.root)

    override fun onBindViewHolder(holder: AlpukatViewHolder, position: Int) {
        val jenisAlpukat = alpukatList[position]

        holder.binding.tvNamaAlpukat.text = jenisAlpukat.namaAlpukat
        Glide.with(holder.itemView.context)
            .load(jenisAlpukat.gambarAlpukat)
            .into(holder.binding.ivNamaAlpukat)
    }

    override fun getItemCount(): Int = alpukatList.size
}