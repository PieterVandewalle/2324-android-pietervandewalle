package com.pietervandewalle.androidapp.data

import com.pietervandewalle.androidapp.model.StudyLocation
import com.pietervandewalle.androidapp.network.GhentApiService
import com.pietervandewalle.androidapp.network.asDomainObjects

interface StudyLocationRepository {
    suspend fun getStudyLocations(searchterm: String? = null): List<StudyLocation>
}
class ApiStudyLocationRepository(private val ghentApiService: GhentApiService) : StudyLocationRepository {
    override suspend fun getStudyLocations(searchterm: String?): List<StudyLocation> {
        // OPENDATASOFT Where clause
        val where: String? = if (searchterm != null) "search(titel,label_1, adres, '$searchterm')" else null

        return ghentApiService.getStudyLocations(where).results.asDomainObjects()
    }
}