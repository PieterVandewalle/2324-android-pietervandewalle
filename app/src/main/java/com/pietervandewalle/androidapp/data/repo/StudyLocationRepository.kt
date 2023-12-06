package com.pietervandewalle.androidapp.data.repo

import com.pietervandewalle.androidapp.data.database.StudyLocationDao
import com.pietervandewalle.androidapp.data.database.asDbStudyLocation
import com.pietervandewalle.androidapp.data.database.asDomainStudyLocation
import com.pietervandewalle.androidapp.data.database.asDomainStudyLocations
import com.pietervandewalle.androidapp.model.StudyLocation
import com.pietervandewalle.androidapp.network.GhentApiService
import com.pietervandewalle.androidapp.network.asDomainObjects
import com.pietervandewalle.androidapp.network.getStudyLocationsAsFlow
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach

interface StudyLocationRepository {
    suspend fun insert(studyLocation: StudyLocation)
    fun getAll(): Flow<List<StudyLocation>>

    fun getAllBySearchTerm(searchTerm: String): Flow<List<StudyLocation>>

    fun getById(id: Int): Flow<StudyLocation>

    suspend fun refresh()
}

class CachingStudyLocationRepository(private val studyLocationDao: StudyLocationDao, private val ghentApiService: GhentApiService) :
    StudyLocationRepository {
    override suspend fun insert(studyLocation: StudyLocation) {
        studyLocationDao.insert(studyLocation.asDbStudyLocation())
    }

    override fun getAll(): Flow<List<StudyLocation>> {
        return studyLocationDao.getAll().map { it.asDomainStudyLocations() }.onEach {
            if (it.isEmpty()) {
                refresh()
            }
        }
    }

    override fun getAllBySearchTerm(searchTerm: String): Flow<List<StudyLocation>> {
        return studyLocationDao.getAllBySearchTerm(searchTerm).map { it.asDomainStudyLocations() }
    }

    override fun getById(id: Int): Flow<StudyLocation> {
        return studyLocationDao.getById(id).map { it.asDomainStudyLocation() }
    }

    override suspend fun refresh() {
        ghentApiService.getStudyLocationsAsFlow().collect {
            for (studyLocation in it.results.asDomainObjects()) {
                insert(studyLocation)
            }
        }
    }
}
