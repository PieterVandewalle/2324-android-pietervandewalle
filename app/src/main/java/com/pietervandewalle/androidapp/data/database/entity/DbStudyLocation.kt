package com.pietervandewalle.androidapp.data.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.pietervandewalle.androidapp.model.GPSCoordinates
import com.pietervandewalle.androidapp.model.StudyLocation

/**
 * Represents an entity in the database for storing study location information.
 *
 * @param id The unique identifier for the study location.
 * @param title The title of the study location.
 * @param label A label or identifier for the study location.
 * @param address The address of the study location.
 * @param totalCapacity The total capacity of the study location.
 * @param reservedAmount The amount of reservations made at the study location.
 * @param readMoreUrl The URL for reading more about the study location.
 * @param imageUrl The URL of the image associated with the study location (optional).
 * @param location The GPS coordinates of the study location.
 * @param reservationTag A tag indicating the reservation status (optional).
 * @param availableTag A tag indicating the availability status (optional).
 */
@Entity(
    tableName = "studyLocations",
)
data class DbStudyLocation(
    @PrimaryKey(autoGenerate = false)
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

/**
 * Converts a [StudyLocation] to a [DbStudyLocation] for storage in the database.
 *
 * @receiver The [StudyLocation] to be converted.
 * @return The corresponding [DbStudyLocation].
 */
fun StudyLocation.asDbStudyLocation(): DbStudyLocation = DbStudyLocation(id = id, title = title, label = label, address = address, totalCapacity = totalCapacity, reservedAmount = reservedAmount, readMoreUrl = readMoreUrl, imageUrl = imageUrl, location = location, reservationTag = reservationTag, availableTag = availableTag)

/**
 * Converts a [DbStudyLocation] to a [StudyLocation] for use in the domain layer.
 *
 * @receiver The [DbStudyLocation] to be converted.
 * @return The corresponding [StudyLocation].
 */
fun DbStudyLocation.asDomainStudyLocation(): StudyLocation = StudyLocation(id = id, title = title, label = label, address = address, totalCapacity = totalCapacity, reservedAmount = reservedAmount, readMoreUrl = readMoreUrl, imageUrl = imageUrl, location = location, reservationTag = reservationTag, availableTag = availableTag)

/**
 * Converts a list of [DbStudyLocation] objects to a list of [StudyLocation] objects for the domain layer.
 *
 * @receiver The list of [DbStudyLocation] objects to be converted.
 * @return The list of corresponding [StudyLocation] objects.
 */
fun List<DbStudyLocation>.asDomainStudyLocations(): List<StudyLocation> = map { it.asDomainStudyLocation() }
