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
    val geo_punt: ApiGPSCoordinates,
    val tag_1: String?,
    val tag_2: String?,
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
            tags = listOfNotNull(it.tag_1, it.tag_2),
        )
    }
    return domainList
}
