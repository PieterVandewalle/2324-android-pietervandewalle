package com.pietervandewalle.androidapp.network

import com.pietervandewalle.androidapp.model.GPSCoordinates
import kotlinx.serialization.Serializable

/**
 * Represents GPS coordinates received from the City of Ghent's API.
 *
 * @property lon The longitude coordinate.
 * @property lat The latitude coordinate.
 */
@Serializable
data class ApiGPSCoordinates(
    val lon: Double,
    val lat: Double,
)

/**
 * Converts [ApiGPSCoordinates] to a domain [GPSCoordinates] object.
 *
 * @receiver The [ApiGPSCoordinates] to convert.
 * @return A domain [GPSCoordinates] object.
 */
fun ApiGPSCoordinates.asDomainObject(): GPSCoordinates {
    return GPSCoordinates(this.lon, this.lat)
}

/**
 * Converts a domain [GPSCoordinates] object to [ApiGPSCoordinates].
 * This function is intended for testing purposes.
 *
 * @receiver The domain [GPSCoordinates] to convert.
 * @return An [ApiGPSCoordinates] object.
 */
fun GPSCoordinates.asApiObject(): ApiGPSCoordinates {
    return ApiGPSCoordinates(longitude, latitude)
}
