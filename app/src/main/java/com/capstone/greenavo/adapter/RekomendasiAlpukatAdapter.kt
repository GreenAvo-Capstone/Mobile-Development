package com.capstone.greenavo.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.capstone.greenavo.data.RekomendasiAlpukat
import com.capstone.greenavo.databinding.ItemRekomendasiBinding

class RekomendasiAlpukatAdapter(private val rekomendasiList: List<RekomendasiAlpukat>): RecyclerView.Adapter<RekomendasiAlpukatAdapter.RekomendasiViewHolder>() {
    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int
    ): RekomendasiViewHolder {
        val binding = ItemRekomendasiBinding.inflate(LayoutInflater.from(viewGroup.context), viewGroup, false)
        return RekomendasiViewHolder(binding)
    }

    class RekomendasiViewHolder(val binding: ItemRekomendasiBinding): RecyclerView.ViewHolder(binding.root)

    override fun onBindViewHolder(holder: RekomendasiViewHolder, position: Int) {
        val rekomendasiAlpukat = rekomendasiList[position]
        holder.binding.tvNamaRekomendasi.text = rekomendasiAlpukat.namaRekomendasi
        holder.binding.tvDeskripsiRekomendasi.text = rekomendasiAlpukat.deskripsi
        Glide.with(holder.itemView.context)
            .load(rekomendasiAlpukat.gambarRekomendasi)
            .into(holder.binding.ivGambarRekomendasi)

        holder.binding.btnFavorite.setOnClickListener {
            Toast.makeText(holder.itemView.context, "Telah menekan tombol favorite", Toast.LENGTH_SHORT).show()
        }
    }

    override fun getItemCount(): Int = rekomendasiList.size
}