package com.pietervandewalle.androidapp.data.database

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import com.pietervandewalle.androidapp.model.CarPark
import com.pietervandewalle.androidapp.model.GPSCoordinates
import java.time.LocalDateTime

@Entity(
    tableName = "carParks",
    indices = [
        Index(
            value = ["name"],
            unique = true,
        ),
    ],
)
data class DbCarPark(
    @PrimaryKey(autoGenerate = true)
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

fun CarPark.asDbCarPark(): DbCarPark =
    DbCarPark(
        id = id, name = name, lastUpdate = lastUpdate, totalCapacity = totalCapacity, availableCapacity = availableCapacity, description = description,
        extraInfo = extraInfo, isOpenNow = isOpenNow, isTemporaryClosed = isTemporaryClosed, isFree = isFree, isInLEZ = isInLEZ, operator = operator, location = location,
    )

fun DbCarPark.asDomainCarPark(): CarPark =
    CarPark(
        id = id, name = name, lastUpdate = lastUpdate, totalCapacity = totalCapacity, availableCapacity = availableCapacity, description = description,
        extraInfo = extraInfo, isOpenNow = isOpenNow, isTemporaryClosed = isTemporaryClosed, isFree = isFree, isInLEZ = isInLEZ, operator = operator, location = location,
    )
fun List<DbCarPark>.asDomainCarParks(): List<CarPark> = map { it.asDomainCarPark() }
