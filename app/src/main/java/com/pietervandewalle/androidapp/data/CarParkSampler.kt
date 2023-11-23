package com.pietervandewalle.androidapp.data

import com.pietervandewalle.androidapp.model.CarPark
import com.pietervandewalle.androidapp.model.GPSCoordinates
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

object CarParkSampler {
    private val sampleCarParks = mutableListOf(
        CarPark(
            name = "Vrijdagmarkt",
            lastUpdate = LocalDateTime.parse("2023-11-23T15:11:05+01:00", DateTimeFormatter.ISO_DATE_TIME),
            totalCapacity = 597,
            availableCapacity = 249,
            description = "Ondergrondse parkeergarage Vrijdagmarkt in Gent",
            extraInfo = null,
            isOpenNow = true,
            isTemporaryClosed = false,
            isFree = false,
            isInLEZ = true,
            operator = "Mobiliteitsbedrijf Gent",
            location = GPSCoordinates(3.726071777876147, 51.05713405953412),
        ),
        CarPark(
            name = "Dok noord",
            lastUpdate = LocalDateTime.parse("2023-11-23T15:11:22+01:00", DateTimeFormatter.ISO_DATE_TIME),
            totalCapacity = 550,
            availableCapacity = 550,
            description = "Ondergrondse parkeergarage Dok noord in Gent",
            extraInfo = "Opgelet! Enkel ingang B ligt buiten LEZ.",
            isOpenNow = false,
            isTemporaryClosed = true,
            isFree = false,
            isInLEZ = false,
            operator = "Indigo",
            location = GPSCoordinates(3.7328389913720565, 51.065684935819604),
        ),
        CarPark(
            name = "Sint-Pietersplein",
            lastUpdate = LocalDateTime.parse("2023-11-23T15:11:18+01:00", DateTimeFormatter.ISO_DATE_TIME),
            totalCapacity = 683,
            availableCapacity = 0,
            description = "Parkeergarage Sint-Pietersplein in Gent",
            extraInfo = null,
            isOpenNow = true,
            isTemporaryClosed = false,
            isFree = false,
            isInLEZ = true,
            operator = "Mobiliteitsbedrijf Gent",
            location = GPSCoordinates(3.7259818626205483, 51.04202834988112),
        ),
        CarPark(
            name = "Reep",
            lastUpdate = LocalDateTime.parse("2023-11-23T15:11:08+01:00", DateTimeFormatter.ISO_DATE_TIME),
            totalCapacity = 449,
            availableCapacity = 9,
            description = "Parkeergarage Reep in Gent",
            extraInfo = null,
            isOpenNow = true,
            isTemporaryClosed = false,
            isFree = false,
            isInLEZ = true,
            operator = "Mobiliteitsbedrijf Gent",
            location = GPSCoordinates(3.7298928896398924, 51.05215838523151),
        ),
    )

    val getAll: () -> MutableList<CarPark> = {
        sampleCarParks
    }
}
