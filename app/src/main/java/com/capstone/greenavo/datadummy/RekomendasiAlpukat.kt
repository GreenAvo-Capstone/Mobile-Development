package com.capstone.greenavo.datadummy

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class RekomendasiAlpukat (
    var id: String = "", // Add an ID field to store the Firestore document ID
    val nama: String = "",
    val deskripsi: String = "",
    val gambar: String = "",
    var isFavorite: Boolean = false
): Parcelable