package com.capstone.greenavo.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.capstone.greenavo.data.ResepAlpukat
import com.capstone.greenavo.databinding.ItemResepBinding

class ResepAlpukatAdapter(private val resepList: List<ResepAlpukat>): RecyclerView.Adapter<ResepAlpukatAdapter.ResepViewHolder>() {
    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ResepViewHolder {
        val binding = ItemResepBinding.inflate(LayoutInflater.from(viewGroup.context), viewGroup, false)
        return ResepViewHolder(binding)
    }

    class ResepViewHolder (val binding: ItemResepBinding): RecyclerView.ViewHolder(binding.root)

    override fun getItemCount(): Int = resepList.size

    override fun onBindViewHolder(holder: ResepViewHolder, position: Int) {
        val resepAlpukat = resepList[position]
        holder.binding.tvNamaResep.text = resepAlpukat.namaResep
        Glide.with(holder.itemView.context)
            .load(resepAlpukat.gambarAlpukat)
            .into(holder.binding.ivNamaResep)
    }
}