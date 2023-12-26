package com.pietervandewalle.androidapp.network

import com.pietervandewalle.androidapp.model.StudyLocation
import kotlinx.serialization.Serializable

/**
 * Represents study location data received from the City of Ghent's API.
 *
 * @property id The ID of the study location.
 * @property titel The title of the study location.
 * @property teaser_img_url The URL of the teaser image for the study location.
 * @property adres The address of the study location.
 * @property totale_capaciteit The total capacity of the study location.
 * @property gereserveerde_plaatsen The number of reserved places at the study location.
 * @property lees_meer The URL for reading more about the study location.
 * @property geo_punt The GPS coordinates of the study location.
 * @property tag_1 The first tag associated with the study location.
 * @property tag_2 The second tag associated with the study location.
 * @property label_1 The first label associated with the study location.
 */
@Serializable
data class ApiStudyLocation(
    val id: Int,
    val titel: String,
    val teaser_img_url: String,
    val adres: String,
    val totale_capaciteit: Int,
    val gereserveerde_plaatsen: Int,
    val lees_meer: String,
    val geo_punt: ApiGPSCoordinates,
    val tag_1: String?,
    val tag_2: String?,
    val label_1: String,
)

/**
 * Converts a list of [ApiStudyLocation] objects from the API
 * to a list of domain [StudyLocation] objects.
 *
 * @receiver The list of [ApiStudyLocation] objects to convert.
 * @return A list of domain [StudyLocation] objects.
 */
fun List<ApiStudyLocation>.asDomainObjects(): List<StudyLocation> {
    val domainList = this.map {
        StudyLocation(
            id = it.id,
            title = it.titel,
            imageUrl = it.teaser_img_url,
            address = it.adres,
            totalCapacity = it.totale_capaciteit,
            location = it.geo_punt.asDomainObject(),
            label = it.label_1,
            reservedAmount = it.gereserveerde_plaatsen,
            readMoreUrl = it.lees_meer,
            reservationTag = it.tag_1,
            availableTag = it.tag_2,
        )
    }
    return domainList
}

/**
 * Converts a list of domain [StudyLocation] objects to a list of [ApiStudyLocation] objects
 * This function is intended for testing purposes.
 *
 * @receiver The list of domain [StudyLocation] objects to convert.
 * @return A list of [ApiStudyLocation] objects.
 */
fun List<StudyLocation>.asApiObjects(): List<ApiStudyLocation> {
    return this.map { studyLocation ->
        ApiStudyLocation(
            id = studyLocation.id,
            titel = studyLocation.title,
            teaser_img_url = studyLocation.imageUrl ?: "",
            adres = studyLocation.address,
            totale_capaciteit = studyLocation.totalCapacity,
            gereserveerde_plaatsen = studyLocation.reservedAmount,
            lees_meer = studyLocation.readMoreUrl,
            geo_punt = studyLocation.location.asApiObject(), // Assuming you have a similar function for ApiGPSCoordinates
            tag_1 = studyLocation.reservationTag,
            tag_2 = studyLocation.availableTag,
            label_1 = studyLocation.label,
        )
    }
}
