package com.pietervandewalle.androidapp.data

import com.pietervandewalle.androidapp.model.StudyLocation
import com.pietervandewalle.androidapp.network.GhentApiService
import com.pietervandewalle.androidapp.network.asDomainObjects

interface StudyLocationRepository {
    suspend fun getStudyLocations(): List<StudyLocation>
}
class ApiStudyLocationRepository(private val ghentApiService: GhentApiService) : StudyLocationRepository {
    override suspend fun getStudyLocations(): List<StudyLocation> {
        return ghentApiService.getStudyLocations().results.asDomainObjects()
    }
}
