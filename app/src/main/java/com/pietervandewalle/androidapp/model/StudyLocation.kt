package com.pietervandewalle.androidapp.model

data class StudyLocation(
    val id: Int,
    val title: String,
    val label: String,
    val address: String,
    val totalCapacity: Int,
    val reservedAmount: Int,
    val readmoreUrl: String,
    val imageUrl: String? = null,
    val location: GPSCoordinates,
    val reservationTag: String? = null,
    val availableTag: String? = null,
)
