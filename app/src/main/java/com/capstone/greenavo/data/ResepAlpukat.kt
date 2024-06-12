package com.capstone.greenavo.data

data class ResepAlpukat (
    val documentId: String? = null,  // Added to hold the document ID from Firestore
    val nama: String? = null,
    val gambar: String? = null,
    val deskripsi: String? = null,
    val bahan: String? = null,
    val cara_membuat: String? = null
)