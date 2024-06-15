package com.capstone.greenavo.datadummy

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class JenisAlpukat (
    val documentId: String,  // Added to hold the document ID from Firestore
    val nama: String,
    val gambar: String,
    val deskripsi: String,
    val sumberLemak: String,
    val tinggiSerat: String,
    val kayaNutrisi: String,
    val antioksidan: String,
    val manfaatKulit: String
): Parcelable