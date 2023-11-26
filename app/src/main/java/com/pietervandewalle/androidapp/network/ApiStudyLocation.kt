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
            readmoreUrl = it.lees_meer,
            reservationTag = it.tag_1,
            availableTag = it.tag_2,
        )
    }
    return domainList
}
