package com.pietervandewalle.androidapp.data.database

import androidx.room.TypeConverter
import com.pietervandewalle.androidapp.model.GPSCoordinates
import java.time.LocalDate

class Converters {
    @TypeConverter
    fun fromTimestamp(value: String?): LocalDate? {
        return value?.let { LocalDate.parse(it) }
    }

    @TypeConverter
    fun dateToTimestamp(date: LocalDate?): String? {
        return date?.toString()
    }

    @TypeConverter
    fun fromGPSCoordinates(coordinates: GPSCoordinates): String {
        return "${coordinates.longitude},${coordinates.latitude}"
    }

    @TypeConverter
    fun toGPSCoordinates(data: String): GPSCoordinates {
        val pieces = data.split(",")
        return GPSCoordinates(pieces[0].toDouble(), pieces[1].toDouble())
    }
}
