package com.capstone.greenavo.data

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class ResultHistory (
    val gambarHasil: String,
    val namaAlpukat: String,
    val kematangan: String,
    val score: String,
) : Parcelable