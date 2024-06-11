package com.capstone.greenavo.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.capstone.greenavo.data.ResultHistory
import com.capstone.greenavo.databinding.ItemHistoryBinding

class HistoryDetectionAdapter (private val listHasilDeteksi: ArrayList<ResultHistory>) : RecyclerView.Adapter<HistoryDetectionAdapter.ListViewHolder>() {
    override fun onCreateViewHolder(
        viewGroup: ViewGroup,
        viewType: Int
    ): ListViewHolder {
        val binding = ItemHistoryBinding.inflate(LayoutInflater.from(viewGroup.context), viewGroup, false)
        return ListViewHolder(binding)
    }

    class ListViewHolder (val binding: ItemHistoryBinding): RecyclerView.ViewHolder(binding.root)

    override fun onBindViewHolder(
        holder: ListViewHolder,
        position: Int
    ) {
        val hasilDeteksi = listHasilDeteksi[position]
        holder.binding.tvNamaAlpukat.text = hasilDeteksi.namaAlpukat
        holder.binding.tvHasilKematangan.text = hasilDeteksi.kematangan
        holder.binding.tvSkor.text = hasilDeteksi.score
        Glide.with(holder.itemView.context)
            .load(hasilDeteksi.gambarHasil)
            .into(holder.binding.ivHasilGambarDeteksi)
        holder.binding.btnDelete.setOnClickListener {
            Toast.makeText(holder.itemView.context, "Telah menekan tombol hapus", Toast.LENGTH_SHORT).show()
        }
    }

    override fun getItemCount(): Int = listHasilDeteksi.size
}