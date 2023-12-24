package com.pietervandewalle.androidapp.repotests

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.pietervandewalle.androidapp.data.database.dao.StudyLocationDao
import com.pietervandewalle.androidapp.data.database.entity.asDbStudyLocation
import com.pietervandewalle.androidapp.data.database.entity.asDomainStudyLocation
import com.pietervandewalle.androidapp.data.repo.CachingStudyLocationRepository
import com.pietervandewalle.androidapp.fake.FakeDataSource
import com.pietervandewalle.androidapp.fake.FakeGhentApiService
import com.pietervandewalle.androidapp.fake.dao.FakeStudyLocationDao
import com.pietervandewalle.androidapp.model.GPSCoordinates
import com.pietervandewalle.androidapp.model.StudyLocation
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.io.IOException

@RunWith(AndroidJUnit4::class)
class StudyLocationRepositoryTest {
    private lateinit var studyLocationDao: StudyLocationDao
    private lateinit var ghentApiService: FakeGhentApiService
    private lateinit var repository: CachingStudyLocationRepository

    @Before
    fun setup() {
        studyLocationDao = FakeStudyLocationDao()
        ghentApiService = FakeGhentApiService(FakeDataSource.apiCarParks, FakeDataSource.apiArticles, FakeDataSource.apiStudyLocations)
        repository = CachingStudyLocationRepository(studyLocationDao, ghentApiService)
        ghentApiService.setShouldReturnNetworkError(false)
    }

    @Test
    fun `getAll returns no study locations when dao is empty`() = runTest {
        assertTrue(repository.getAll().first().isEmpty())
    }

    @Test
    fun `automatically refreshes and populates dao when initially empty`() = runTest {
        assertTrue(repository.getAll().first().isEmpty())
        assertTrue(repository.getAll().first().isNotEmpty())
    }

    @Test
    fun `correctly inserts a study location`() = runTest {
        val studyLocation = StudyLocation(
            1, "Test Location", "Test Address", "teststraat 123, 9000 Gent",
            20, 10, "https://test.be/readMore", "https://test.be/image", GPSCoordinates(3.3242, 4.342424), "", "",
        )
        repository.insert(studyLocation)

        val insertedLocation = studyLocationDao.getById(studyLocation.id).first()
        assertEquals(studyLocation, insertedLocation.asDomainStudyLocation())
    }

    @Test
    fun `returns correct study location by id`() = runTest {
        studyLocationDao = FakeStudyLocationDao(FakeDataSource.domainStudyLocations.map { it.asDbStudyLocation() })
        repository = CachingStudyLocationRepository(studyLocationDao, ghentApiService)
        val expectedLocation = FakeDataSource.domainStudyLocations.first()
        val retrievedLocation = repository.getById(expectedLocation.id).first()
        assertEquals(expectedLocation, retrievedLocation)
    }

    @Test(expected = IOException::class)
    fun `refresh throws error for UI to catch`() = runTest {
        ghentApiService.setShouldReturnNetworkError(true)
        repository.refresh()
    }

    @Test
    fun `refresh inserts new study locations`() = runTest {
        repository.refresh()
        val apiLocations = ghentApiService.getStudyLocations().results.size
        val daoLocations = studyLocationDao.getAll().first().size
        assertEquals(apiLocations, daoLocations)
    }

    @Test
    fun `getAllBySearchTerm returns matching study locations`() = runTest {
        // Search for a term that should return at least one match
        studyLocationDao = FakeStudyLocationDao(FakeDataSource.domainStudyLocations.map { it.asDbStudyLocation() })
        repository = CachingStudyLocationRepository(studyLocationDao, ghentApiService)

        val searchResults = repository.getAllBySearchTerm(FakeDataSource.domainStudyLocations.first().title).first()

        assertEquals(1, searchResults.size)
    }
}
