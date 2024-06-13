package com.capstone.greenavo.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.capstone.greenavo.R
import com.capstone.greenavo.data.RekomendasiAlpukat
import com.capstone.greenavo.databinding.ItemRekomendasiBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions

class RekomendasiAlpukatAdapter(private val listRekomendasi: MutableList<RekomendasiAlpukat>) : RecyclerView.Adapter<RekomendasiAlpukatAdapter.RekomendasiViewHolder>() {
    private val db = FirebaseFirestore.getInstance()
    private val currentUser = FirebaseAuth.getInstance().currentUser

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): RekomendasiViewHolder {
        val binding = ItemRekomendasiBinding.inflate(LayoutInflater.from(viewGroup.context), viewGroup, false)
        return RekomendasiViewHolder(binding)
    }

    inner class RekomendasiViewHolder(val binding: ItemRekomendasiBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onBindViewHolder(holder: RekomendasiViewHolder, position: Int) {
        val rekomendasi = listRekomendasi[position]

        holder.binding.tvNamaRekomendasi.text = rekomendasi.nama
        holder.binding.tvDeskripsiRekomendasi.text = rekomendasi.deskripsi
        Glide.with(holder.binding.ivGambarRekomendasi.context)
            .load(rekomendasi.gambar)
            .into(holder.binding.ivGambarRekomendasi)

        // Check and set the favorite status
        updateFavoriteStatusFromFirestore(rekomendasi, holder)

        holder.binding.btnFavorite.setOnClickListener {
            clickFavorite(rekomendasi, holder)
        }
    }

    private fun clickFavorite(rekomendasi: RekomendasiAlpukat, holder: RekomendasiViewHolder) {
        if (currentUser == null) {
            // Handle user not logged in
            return
        }

        val userId = currentUser.uid
        val favoriteCollection = db.collection("users").document(userId).collection("favorite")

        if (rekomendasi.isFavorite) {
            // Remove from favorites
            val docId = rekomendasi.id ?: ""
            favoriteCollection.document(docId).delete().addOnSuccessListener {
                rekomendasi.isFavorite = false
                updateFavoriteIcon(holder, false)
            }.addOnFailureListener {
                // Handle failure
            }
        } else {
            // Add to favorites
            val favoriteData = hashMapOf(
                "nama" to rekomendasi.nama,
                "gambar" to rekomendasi.gambar,
                "deskripsi" to rekomendasi.deskripsi
            )

            val docId = rekomendasi.id ?: ""
            favoriteCollection.document(docId).set(favoriteData, SetOptions.merge()).addOnSuccessListener {
                rekomendasi.isFavorite = true
                updateFavoriteIcon(holder, true)
            }.addOnFailureListener {
                // Handle failure
            }
        }
    }

    private fun updateFavoriteStatusFromFirestore(rekomendasi: RekomendasiAlpukat, holder: RekomendasiViewHolder) {
        if (currentUser == null) {
            // Handle user not logged in
            return
        }

        val userId = currentUser.uid
        val favoriteDocRef = db.collection("users").document(userId).collection("favorite").document(rekomendasi.id ?: "")

        favoriteDocRef.get().addOnSuccessListener { document ->
            if (document.exists()) {
                rekomendasi.isFavorite = true
                updateFavoriteIcon(holder, true)
            } else {
                rekomendasi.isFavorite = false
                updateFavoriteIcon(holder, false)
            }
        }
    }

    private fun updateFavoriteIcon(holder: RekomendasiViewHolder, isFavorite: Boolean) {
        if (isFavorite) {
            holder.binding.btnFavorite.setImageResource(R.drawable.ic_favorite_fill_24)
        } else {
            holder.binding.btnFavorite.setImageResource(R.drawable.ic_favorite_border_24)
        }
    }

    override fun getItemCount(): Int = listRekomendasi.size
}
