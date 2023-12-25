package com.pietervandewalle.androidapp.data.repo

import com.pietervandewalle.androidapp.data.database.dao.StudyLocationDao
import com.pietervandewalle.androidapp.data.database.entity.asDbStudyLocation
import com.pietervandewalle.androidapp.data.database.entity.asDomainStudyLocation
import com.pietervandewalle.androidapp.data.database.entity.asDomainStudyLocations
import com.pietervandewalle.androidapp.model.StudyLocation
import com.pietervandewalle.androidapp.network.GhentApiService
import com.pietervandewalle.androidapp.network.asDomainObjects
import com.pietervandewalle.androidapp.network.getStudyLocationsAsFlow
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach

/**
 * Interface for managing study locations.
 */
interface StudyLocationRepository {
    /**
     * Retrieves all study locations from the repository as a Flow of lists.
     *
     * @return A Flow emitting a list of study locations.
     */
    fun getAll(): Flow<List<StudyLocation>>

    /**
     * Retrieves study locations that match a search term from the repository as a Flow of lists.
     *
     * @param searchTerm The search term to filter study locations.
     * @return A Flow emitting a list of study locations matching the search term.
     */
    fun getAllBySearchTerm(searchTerm: String): Flow<List<StudyLocation>>

    /**
     * Retrieves a study location by its unique identifier from the repository as a Flow.
     *
     * @param id The unique identifier of the study location to retrieve.
     * @return A Flow emitting the requested study location.
     */
    fun getById(id: Int): Flow<StudyLocation>

    /**
     * Inserts a study location into the repository.
     *
     * @param studyLocation The study location to insert.
     */
    suspend fun insert(studyLocation: StudyLocation)

    /**
     * Refreshes the repository, typically by fetching updated data from a remote source.
     */
    suspend fun refresh()
}

/**
 * Implementation of [StudyLocationRepository] that caches study locations using a local database
 * (represented by [studyLocationDao]) and fetches data from a remote source (represented by [ghentApiService]).
 *
 * @param studyLocationDao The data access object for the local database.
 * @param ghentApiService The API service for fetching remote data.
 */
class CachingStudyLocationRepository(private val studyLocationDao: StudyLocationDao, private val ghentApiService: GhentApiService) :
    StudyLocationRepository {

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

    override suspend fun insert(studyLocation: StudyLocation) {
        studyLocationDao.insert(studyLocation.asDbStudyLocation())
    }

    override suspend fun refresh() {
        ghentApiService.getStudyLocationsAsFlow().collect {
            for (studyLocation in it.results.asDomainObjects()) {
                insert(studyLocation)
            }
        }
    }
}
