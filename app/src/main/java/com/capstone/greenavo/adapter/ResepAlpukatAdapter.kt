package com.capstone.greenavo.adapter

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.capstone.greenavo.data.ResepAlpukat
import com.capstone.greenavo.databinding.ItemResepBinding
import com.capstone.greenavo.ui.detail.DetailResepAlpukatActivity

class ResepAlpukatAdapter(private val resepList: List<ResepAlpukat>): RecyclerView.Adapter<ResepAlpukatAdapter.ResepViewHolder>() {
    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ResepViewHolder {
        val binding = ItemResepBinding.inflate(LayoutInflater.from(viewGroup.context), viewGroup, false)
        return ResepViewHolder(binding)
    }

    inner class ResepViewHolder (val binding: ItemResepBinding): RecyclerView.ViewHolder(binding.root)

    override fun onBindViewHolder(holder: ResepViewHolder, position: Int) {
        val resepAlpukat = resepList[position]

        holder.binding.tvNamaResep.text = resepAlpukat.nama
        Glide.with(holder.itemView.context)
            .load(resepAlpukat.gambar)
            .into(holder.binding.ivNamaResep)

        holder.itemView.setOnClickListener {
            val context = holder.itemView.context
            val intent = Intent(context, DetailResepAlpukatActivity::class.java)
            intent.putExtra("DOCUMENT_ID", resepAlpukat.documentId) // Assuming you have a documentId field
            context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int = resepList.size
}