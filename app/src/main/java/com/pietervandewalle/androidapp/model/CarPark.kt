package com.pietervandewalle.androidapp.model

import java.lang.reflect.Array.get
import java.time.LocalDateTime

data class CarPark(
    val name: String,
    val lastUpdate: LocalDateTime,
    val totalCapacity: Int,
    val availableCapacity: Int,
    val description: String,
    val extraInfo: String?,
    val isOpenNow: Boolean,
    val isTemporaryClosed: Boolean,
    val isFree: Boolean,
    val isInLEZ: Boolean,
    val operator: String,
    val location: GPSCoordinates,
)

val CarPark.isAlmostFull: Boolean
    get() = availableCapacity <= 10

val CarPark.isFull: Boolean
    get() = availableCapacity == 0
