package com.capstone.greenavo.adapter

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.capstone.greenavo.data.JenisAlpukat
import com.capstone.greenavo.databinding.ItemAlpukatBinding
import com.capstone.greenavo.ui.detail.DetailJenisAlpukatActivity
import com.capstone.greenavo.ui.detail.DetailJenisAlpukatActivity.Companion.KEY_JENIS_ALPUKAT

class JenisAlpukatAdapter(private val alpukatList: List<JenisAlpukat>): RecyclerView.Adapter<JenisAlpukatAdapter.AlpukatViewHolder>() {
    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): AlpukatViewHolder {
        val binding = ItemAlpukatBinding.inflate(LayoutInflater.from(viewGroup.context), viewGroup, false)
        return AlpukatViewHolder(binding)
    }

    class AlpukatViewHolder(val binding: ItemAlpukatBinding): RecyclerView.ViewHolder(binding.root)

    override fun onBindViewHolder(holder: AlpukatViewHolder, position: Int) {
        val jenisAlpukat = alpukatList[position]
        holder.binding.tvNamaAlpukat.text = jenisAlpukat.namaAlpukat
        Glide.with(holder.itemView.context)
            .load(jenisAlpukat.gambarAlpukat)
            .into(holder.binding.ivNamaAlpukat)

        //Pindah ke Detail
        holder.itemView.setOnClickListener {
            val intentDetail = Intent(holder.itemView.context, DetailJenisAlpukatActivity::class.java)
            intentDetail.putExtra(KEY_JENIS_ALPUKAT, alpukatList[holder.adapterPosition])
            holder.itemView.context.startActivity(intentDetail)
        }
    }

    override fun getItemCount(): Int = alpukatList.size
}