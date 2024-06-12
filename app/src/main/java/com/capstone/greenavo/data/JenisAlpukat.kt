package com.capstone.greenavo.data

data class JenisAlpukat (
    val documentId: String? = null,  // Added to hold the document ID from Firestore
    val nama: String? = null,
    val gambar: String? = null,
    val deskripsi: String? = null,
    val sumberLemak: String? = null,
    val tinggiSerat: String? = null,
    val kayaNutrisi: String? = null,
    val antioksidan: String? = null,
    val manfaatKulit: String? = null
)