package com.pietervandewalle.androidapp.network

import com.pietervandewalle.androidapp.model.StudyLocation
import kotlinx.serialization.Serializable

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
