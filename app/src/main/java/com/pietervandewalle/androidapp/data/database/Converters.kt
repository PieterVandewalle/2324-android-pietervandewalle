package com.pietervandewalle.androidapp.data.database

import androidx.room.TypeConverter
import com.pietervandewalle.androidapp.model.GPSCoordinates
import java.time.LocalDate
import java.time.LocalDateTime

/**
 * This class provides type converters for Room database to handle conversions between complex data types and their
 * representations in the database.
 */
class Converters {
    /**
     * Converts a [String] representation of a date to a [LocalDate] object.
     *
     * @param value The [String] representation of the date.
     * @return The corresponding [LocalDate] or null if the input is null.
     */
    @TypeConverter
    fun localDateFromTimestamp(value: String?): LocalDate? {
        return value?.let { LocalDate.parse(it) }
    }

    /**
     * Converts a [LocalDate] object to its [String] representation.
     *
     * @param date The [LocalDate] object.
     * @return The [String] representation of the date or null if the input is null.
     */
    @TypeConverter
    fun localDateToTimestamp(date: LocalDate?): String? {
        return date?.toString()
    }

    /**
     * Converts a [String] representation of a date and time to a [LocalDateTime] object.
     *
     * @param value The [String] representation of the date and time.
     * @return The corresponding [LocalDateTime] or null if the input is null.
     */
    @TypeConverter
    fun fromTimestampLocalDateTime(value: String?): LocalDateTime? {
        return value?.let { LocalDateTime.parse(it) }
    }

    /**
     * Converts a [LocalDateTime] object to its [String] representation.
     *
     * @param date The [LocalDateTime] object.
     * @return The [String] representation of the date and time or null if the input is null.
     */
    @TypeConverter
    fun localDateTimeToTimestamp(date: LocalDateTime?): String? {
        return date?.toString()
    }

    /**
     * Converts a [GPSCoordinates] object to its [String] representation.
     *
     * @param coordinates The [GPSCoordinates] object.
     * @return The [String] representation of the GPS coordinates.
     */
    @TypeConverter
    fun fromGPSCoordinates(coordinates: GPSCoordinates): String {
        return "${coordinates.longitude},${coordinates.latitude}"
    }

    /**
     * Converts a [String] representation of GPS coordinates to a [GPSCoordinates] object.
     *
     * @param data The [String] representation of GPS coordinates.
     * @return The corresponding [GPSCoordinates] object.
     */
    @TypeConverter
    fun toGPSCoordinates(data: String): GPSCoordinates {
        val pieces = data.split(",")
        return GPSCoordinates(pieces[0].toDouble(), pieces[1].toDouble())
    }
}
