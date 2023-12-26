package com.pietervandewalle.androidapp.model

import java.time.LocalDateTime

/**
 * Represents a car park with essential information.
 *
 * @property id The unique identifier for the car park. Default is 0.
 * @property name The name of the car park.
 * @property lastUpdate The date and time when the car park information was last updated.
 * @property totalCapacity The total capacity of the car park.
 * @property availableCapacity The available capacity of the car park.
 * @property description A brief description of the car park.
 * @property extraInfo Additional information about the car park (optional). Default is null.
 * @property isOpenNow Indicates whether the car park is currently open.
 * @property isTemporaryClosed Indicates whether the car park is temporarily closed.
 * @property isFree Indicates whether the car park is free.
 * @property isInLEZ Indicates whether the car park is located within a Low Emission Zone (LEZ).
 * @property operator The operator or organization managing the car park.
 * @property location The GPS coordinates of the car park's location.
 */
data class CarPark(
    val id: Int = 0,
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

/**
 * Indicates whether the car park is almost full, typically when the available capacity is 10 or less.
 */
val CarPark.isAlmostFull: Boolean
    get() = availableCapacity <= 10

/**
 * Indicates whether the car park is full, when the available capacity is 0.
 */
val CarPark.isFull: Boolean
    get() = availableCapacity <= 0
