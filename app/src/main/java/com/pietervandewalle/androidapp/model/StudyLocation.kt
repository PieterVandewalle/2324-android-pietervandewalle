package com.pietervandewalle.androidapp.model

/**
 * Represents a study location with essential information.
 *
 * @property id The unique identifier for the study location.
 * @property title The title or name of the study location.
 * @property label A label or category associated with the study location.
 * @property address The address of the study location.
 * @property totalCapacity The total capacity of the study location.
 * @property reservedAmount The amount of space reserved at the study location.
 * @property readMoreUrl The URL for obtaining more information about the study location.
 * @property imageUrl The URL of an image representing the study location (optional). Default is null.
 * @property location The GPS coordinates specifying the location of the study location.
 * @property reservationTag A tag or label indicating reservation status (optional). Default is null.
 * @property availableTag A tag or label indicating availability status (optional). Default is null.
 */
data class StudyLocation(
    val id: Int,
    val title: String,
    val label: String,
    val address: String,
    val totalCapacity: Int,
    val reservedAmount: Int,
    val readMoreUrl: String,
    val imageUrl: String? = null,
    val location: GPSCoordinates,
    val reservationTag: String? = null,
    val availableTag: String? = null,
)
