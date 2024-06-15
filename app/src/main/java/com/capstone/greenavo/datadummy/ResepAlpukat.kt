package com.capstone.greenavo.datadummy

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class ResepAlpukat (
    val documentId: String,  // Added to hold the document ID from Firestore
    val nama: String,
    val gambar: String,
    val deskripsi: String,
    val bahan: String,
    val cara_membuat: String
): Parcelable