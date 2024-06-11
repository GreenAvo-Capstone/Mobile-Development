package com.capstone.greenavo.data

data class ResultHistory (
    var id: String = "", // Add an ID field to store the Firestore document ID
    val image_url: String = "",
    val kematangan: String = "",
    val score: String = ""
)