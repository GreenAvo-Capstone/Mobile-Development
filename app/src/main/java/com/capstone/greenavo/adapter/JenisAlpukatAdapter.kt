package com.capstone.greenavo.adapter

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.capstone.greenavo.data.JenisAlpukat
import com.capstone.greenavo.databinding.ItemAlpukatBinding
import com.capstone.greenavo.ui.detail.DetailJenisAlpukatActivity

class JenisAlpukatAdapter(private val alpukatList: List<JenisAlpukat>): RecyclerView.Adapter<JenisAlpukatAdapter.AlpukatViewHolder>() {
    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): AlpukatViewHolder {
        val binding = ItemAlpukatBinding.inflate(LayoutInflater.from(viewGroup.context), viewGroup, false)
        return AlpukatViewHolder(binding)
    }

    inner class AlpukatViewHolder(val binding: ItemAlpukatBinding): RecyclerView.ViewHolder(binding.root)

    override fun onBindViewHolder(holder: AlpukatViewHolder, position: Int) {
        val jenisAlpukat = alpukatList[position]

        holder.binding.tvNamaAlpukat.text = jenisAlpukat.nama
        Glide.with(holder.itemView.context)
            .load(jenisAlpukat.gambar)
            .into(holder.binding.ivNamaAlpukat)

        holder.itemView.setOnClickListener {
            val context = holder.itemView.context
            val intent = Intent(context, DetailJenisAlpukatActivity::class.java)
            intent.putExtra("DOCUMENT_ID", jenisAlpukat.documentId) // Assuming you have a documentId field
            context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int = alpukatList.size
}