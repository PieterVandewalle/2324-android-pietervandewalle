package com.pietervandewalle.androidapp.network

import com.pietervandewalle.androidapp.model.CarPark
import kotlinx.serialization.Serializable
import java.time.OffsetDateTime
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter

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
