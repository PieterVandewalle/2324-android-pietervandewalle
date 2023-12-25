package com.pietervandewalle.androidapp.network

import com.pietervandewalle.androidapp.model.CarPark
import kotlinx.serialization.Serializable
import java.time.OffsetDateTime
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter

/**
 * Represents car park data received from the City of Ghent's API.
 *
 * @property name The name of the car park.
 * @property lastupdate The last update timestamp of the car park data in ISO date-time format.
 * @property totalcapacity The total capacity of the car park.
 * @property availablecapacity The available capacity of the car park.
 * @property type The type of the car park.
 * @property description The description of the car park.
 * @property openingtimesdescription The opening times description of the car park.
 * @property isopennow An indicator of whether the car park is currently open.
 * @property temporaryclosed An indicator of whether the car park is temporarily closed.
 * @property operatorinformation Information about the car park operator.
 * @property freeparking An indicator of whether the car park offers free parking.
 * @property location The GPS coordinates of the car park.
 * @property text Additional text information about the car park.
 * @property categorie The category of the car park.
 */
@Serializable
data class ApiCarPark(
    val name: String,
    val lastupdate: String,
    val totalcapacity: Int,
    val availablecapacity: Int,
    val type: String,
    val description: String,
    val openingtimesdescription: String,
    val isopennow: Int,
    val temporaryclosed: Int,
    val operatorinformation: String,
    val freeparking: Int,
    val location: ApiGPSCoordinates,
    val text: String?,
    val categorie: String,
)

/**
 * Converts a list of [ApiCarPark] objects from the API
 * to a list of domain [CarPark] objects.
 *
 * @receiver The list of [ApiCarPark] objects to convert.
 * @return A list of domain [CarPark] objects.
 */
fun List<ApiCarPark>.asDomainObjects(): List<CarPark> {
    var domainList = this.map {
        CarPark(
            name = it.name,
            lastUpdate = OffsetDateTime.parse(it.lastupdate, DateTimeFormatter.ISO_DATE_TIME).atZoneSameInstant(ZoneOffset.UTC).toLocalDateTime(),
            totalCapacity = it.totalcapacity,
            availableCapacity = it.availablecapacity,
            description = it.description.replace("? ", ""),
            extraInfo = it.text,
            isOpenNow = it.isopennow == 1,
            isTemporaryClosed = it.temporaryclosed == 1,
            operator = it.operatorinformation,
            isInLEZ = it.categorie.contains("in LEZ"),
            isFree = it.freeparking == 1,
            location = it.location.asDomainObject(),
        )
    }
    return domainList
}

/**
 * Converts a list of domain [CarPark] objects to a list of [ApiCarPark] objects
 * This function is intended for testing purposes.
 *
 * @receiver The list of domain [CarPark] objects to convert.
 * @return A list of [ApiCarPark] objects.
 */
fun List<CarPark>.asApiObjects(): List<ApiCarPark> {
    return this.map { carPark ->
        ApiCarPark(
            name = carPark.name,
            lastupdate = carPark.lastUpdate.atOffset(ZoneOffset.UTC).format(DateTimeFormatter.ISO_DATE_TIME),
            totalcapacity = carPark.totalCapacity,
            availablecapacity = carPark.availableCapacity,
            type = "",
            description = carPark.description,
            openingtimesdescription = "",
            isopennow = if (carPark.isOpenNow) 1 else 0,
            temporaryclosed = if (carPark.isTemporaryClosed) 1 else 0,
            operatorinformation = carPark.operator,
            freeparking = if (carPark.isFree) 1 else 0,
            location = carPark.location.asApiObject(),
            text = carPark.extraInfo,
            categorie = if (carPark.isInLEZ) "in LEZ" else "not in LEZ",
        )
    }
}
