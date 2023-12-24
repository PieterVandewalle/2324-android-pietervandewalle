package com.pietervandewalle.androidapp.data.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.pietervandewalle.androidapp.model.GPSCoordinates
import com.pietervandewalle.androidapp.model.StudyLocation

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

fun StudyLocation.asDbStudyLocation(): DbStudyLocation = DbStudyLocation(id = id, title = title, label = label, address = address, totalCapacity = totalCapacity, reservedAmount = reservedAmount, readMoreUrl = readMoreUrl, imageUrl = imageUrl, location = location, reservationTag = reservationTag, availableTag = availableTag)
fun DbStudyLocation.asDomainStudyLocation(): StudyLocation = StudyLocation(id = id, title = title, label = label, address = address, totalCapacity = totalCapacity, reservedAmount = reservedAmount, readMoreUrl = readMoreUrl, imageUrl = imageUrl, location = location, reservationTag = reservationTag, availableTag = availableTag)
fun List<DbStudyLocation>.asDomainStudyLocations(): List<StudyLocation> = map { it.asDomainStudyLocation() }
