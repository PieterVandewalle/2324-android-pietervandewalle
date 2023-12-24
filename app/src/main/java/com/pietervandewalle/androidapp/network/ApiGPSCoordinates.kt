package com.pietervandewalle.androidapp.network

import com.pietervandewalle.androidapp.model.GPSCoordinates
import kotlinx.serialization.Serializable

@Serializable
data class ApiGPSCoordinates(
    val lon: Double,
    val lat: Double,
)

fun ApiGPSCoordinates.asDomainObject(): GPSCoordinates {
    return GPSCoordinates(this.lon, this.lat)
}

fun GPSCoordinates.asApiObject(): ApiGPSCoordinates {
    return ApiGPSCoordinates(longitude, latitude)
}
