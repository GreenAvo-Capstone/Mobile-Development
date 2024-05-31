package com.capstone.greenavo.data

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class JenisAlpukat (
    val namaAlpukat: String,
    val gambarAlpukat: String,
    val deskripsiSatu: String,
    val deskripsiDua: String,
    val sumberLemakSehatSerat: String,
    val kayaAkanNutrisi: String,
    val tinggiSerat: String,
    val antioksidan: String,
    val manfaatKulit: String
) : Parcelable