package com.capstone.greenavo.datadummy

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class ResultHistory (
    var id: String = "", // Add an ID field to store the Firestore document ID
    val image_url: String = "",
    val label: String = "",
    val score: String = ""
): Parcelable