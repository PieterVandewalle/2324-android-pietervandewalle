package com.pietervandewalle.androidapp.data.database

import androidx.room.TypeConverter
import com.pietervandewalle.androidapp.model.GPSCoordinates
import java.time.LocalDate
import java.time.LocalDateTime

class Converters {
    @TypeConverter
    fun localDateFromTimestamp(value: String?): LocalDate? {
        return value?.let { LocalDate.parse(it) }
    }

    @TypeConverter
    fun localDateToTimestamp(date: LocalDate?): String? {
        return date?.toString()
    }

    @TypeConverter
    fun fromTimestampLocalDateTime(value: String?): LocalDateTime? {
        return value?.let { LocalDateTime.parse(it) }
    }

    @TypeConverter
    fun localDateTimeToTimestamp(date: LocalDateTime?): String? {
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
