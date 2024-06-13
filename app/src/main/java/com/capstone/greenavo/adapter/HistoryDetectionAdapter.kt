package com.capstone.greenavo.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.capstone.greenavo.R
import com.capstone.greenavo.data.ResultHistory
import com.capstone.greenavo.databinding.ItemHistoryBinding
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.firestore

class HistoryDetectionAdapter(private val listHasilDeteksi: MutableList<ResultHistory>) : RecyclerView.Adapter<HistoryDetectionAdapter.ListViewHolder>() {
    override fun onCreateViewHolder(
        viewGroup: ViewGroup,
        viewType: Int
    ): ListViewHolder {
        val binding = ItemHistoryBinding.inflate(LayoutInflater.from(viewGroup.context), viewGroup, false)
        return ListViewHolder(binding)
    }

    inner class ListViewHolder (val binding: ItemHistoryBinding): RecyclerView.ViewHolder(binding.root)

    override fun onBindViewHolder(holder: ListViewHolder, position: Int) {
        val hasilDeteksi = listHasilDeteksi[position]

        holder.binding.tvHasilKematangan.text = hasilDeteksi.label
        // Ubah warna teks berdasarkan label hasil
        val textColor = when (hasilDeteksi.label) {
            "Belum Matang" -> ContextCompat.getColor(holder.binding.root.context, R.color.low)
            "Setengah Matang" -> ContextCompat.getColor(holder.binding.root.context, R.color.medium)
            "Matang" -> ContextCompat.getColor(holder.binding.root.context, R.color.high)
            else -> ContextCompat.getColor(holder.binding.root.context, android.R.color.black)
        }

        // Set warna teks ke TextView
        holder.binding.tvHasilKematangan.setTextColor(textColor)

        holder.binding.tvSkor.text = hasilDeteksi.score
        Glide.with(holder.itemView.context)
            .load(hasilDeteksi.image_url)
            .into(holder.binding.ivHasilGambarDeteksi)

        //Hapus Data
        holder.binding.btnDelete.setOnClickListener {
            // Delete the document from Firestore using the document ID
            deleteResult(holder.adapterPosition)
        }
    }
    private fun deleteResult(position: Int) {
        val result = listHasilDeteksi[position]
        val db = Firebase.firestore
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: "default_user_id" // Ganti dengan cara Anda mendapatkan ID pengguna

        // Delete the document from Firestore using the document ID
        db.collection("users").document(userId)
            .collection("hasil_deteksi")
            .document(result.id)
            .delete()
            .addOnSuccessListener {
                listHasilDeteksi.removeAt(position) // Remove the item from the list
                notifyItemRemoved(position) // Notify the adapter of item removal
                Log.d("ResultDetectionAdapter", "DocumentSnapshot successfully deleted!")
            }
            .addOnFailureListener { e ->
                Log.w("ResultDetectionAdapter", "Error deleting document", e)
            }
    }
    override fun getItemCount(): Int = listHasilDeteksi.size
}