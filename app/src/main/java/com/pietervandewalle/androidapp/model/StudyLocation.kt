package com.pietervandewalle.androidapp.model

data class StudyLocation(
    val id: Int,
    val title: String,
    val address: String,
    val totalCapacity: Int,
    val imageUrl: String? = null,
    val location: GPSCoordinates,
    val tags: List<String>,
)
