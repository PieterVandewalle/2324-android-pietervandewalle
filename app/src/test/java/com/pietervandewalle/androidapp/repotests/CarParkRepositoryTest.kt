package com.pietervandewalle.androidapp.repotests

import android.content.Context
import android.util.Log
import androidx.test.core.app.ApplicationProvider
import androidx.work.Configuration
import androidx.work.WorkInfo
import androidx.work.WorkManager
import androidx.work.WorkQuery
import androidx.work.testing.SynchronousExecutor
import androidx.work.testing.WorkManagerTestInitHelper
import com.pietervandewalle.androidapp.data.database.entity.asDbCarPark
import com.pietervandewalle.androidapp.data.database.entity.asDomainCarPark
import com.pietervandewalle.androidapp.data.repo.CachingCarParkRepository
import com.pietervandewalle.androidapp.fake.FakeDataSource
import com.pietervandewalle.androidapp.fake.FakeGhentApiService
import com.pietervandewalle.androidapp.fake.dao.FakeCarParkDao
import com.pietervandewalle.androidapp.model.CarPark
import com.pietervandewalle.androidapp.model.GPSCoordinates
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.withContext
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Assert.fail
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import java.io.IOException
import java.time.LocalDateTime
import java.util.concurrent.TimeUnit

@RunWith(RobolectricTestRunner::class)
class CarParkRepositoryTest {
    private val fakeApiService = FakeGhentApiService(FakeDataSource.apiCarParks, FakeDataSource.apiArticles, FakeDataSource.apiStudyLocations)
    private lateinit var context: Context
    private lateinit var workManager: WorkManager

    @Before
    fun setup() {
        context = ApplicationProvider.getApplicationContext()

        val config = Configuration.Builder()
            .setMinimumLoggingLevel(Log.DEBUG)
            .setExecutor(SynchronousExecutor())
            .build()

        WorkManagerTestInitHelper.initializeTestWorkManager(context, config)
        workManager = WorkManager.getInstance(context)
        fakeApiService.setShouldReturnNetworkError(false)
    }

    @After
    fun teardown() {
        WorkManagerTestInitHelper.closeWorkDatabase()
    }

    @Test
    fun `getAll returns no car parks when dao is empty`() = runTest {
        val fakeDao = FakeCarParkDao()
        val repo = CachingCarParkRepository(
            fakeDao,
            fakeApiService,
            context,
        )

        val carParksFlow = repo.getAll()

        assertTrue(carParksFlow.first().isEmpty())
    }

    @Test
    fun `automatically refreshes and populates dao when initially empty`() = runTest {
        val fakeDao = FakeCarParkDao() // Initially empty
        val repo = CachingCarParkRepository(
            fakeDao,
            fakeApiService, // Returns car parks
            context,
        )

        val initialCarParksFlow = repo.getAll()
        assertTrue(initialCarParksFlow.first().isEmpty())

        val updatedCarParksFlow = repo.getAll()
        assertTrue(updatedCarParksFlow.first().isNotEmpty())
    }

    @Test
    fun `getAll enqueues periodic refresh worker`() = runTest {
        val fakeDao = FakeCarParkDao()
        val repo = CachingCarParkRepository(
            fakeDao,
            fakeApiService,
            context,
        )

        repo.getAll().first()

        val workInfosWithTag = withContext(Dispatchers.IO) {
            workManager.getWorkInfos(WorkQuery.fromUniqueWorkNames("refreshCarParksPeriodically"))
                .get(5L, TimeUnit.SECONDS)
        }
        assertNotNull(workInfosWithTag.firstOrNull())

        val workInfo = workInfosWithTag.first()
        assertTrue(workInfo.state == WorkInfo.State.ENQUEUED || workInfo.state == WorkInfo.State.RUNNING)
    }

    @Test
    fun `getAll enqueues periodic notification worker`() = runTest {
        val fakeDao = FakeCarParkDao()
        val repo = CachingCarParkRepository(
            fakeDao,
            fakeApiService,
            context,
        )

        repo.getAll().first()

        val workInfosWithTag = withContext(Dispatchers.IO) {
            workManager.getWorkInfos(
                WorkQuery.fromUniqueWorkNames("notifyAboutCarParksPeriodically"),
            ).get(5L, TimeUnit.SECONDS)
        }
        assertNotNull(workInfosWithTag.firstOrNull())

        val workInfo = workInfosWithTag.first()
        assertTrue(workInfo.state == WorkInfo.State.ENQUEUED || workInfo.state == WorkInfo.State.RUNNING)
    }

    @Test
    fun `correctly inserts a car park`() = runTest {
        val fakeDao = FakeCarParkDao()
        val repo = CachingCarParkRepository(fakeDao, fakeApiService, context)

        val carParkToInsert = CarPark(111, "Test Park", LocalDateTime.now(), 50, 20, "test carpark description", "extra info", true, false, true, true, "testOperator", GPSCoordinates(3.43243423, 53.43242423)) // Assuming CarPark data class
        repo.insert(carParkToInsert)

        val insertedCarPark = fakeDao.getById(carParkToInsert.id).first()
        assertEquals(carParkToInsert, insertedCarPark.asDomainCarPark())
    }

    @Test
    fun `returns correct car park by id`() = runTest {
        val fakeDao = FakeCarParkDao(FakeDataSource.domainCarParks.map { it.asDbCarPark() })
        val repo = CachingCarParkRepository(fakeDao, fakeApiService, context)

        val carParkToFind = FakeDataSource.domainCarParks.first()

        val retrievedCarParkFlow = repo.getById(carParkToFind.id)
        val retrievedCarPark = retrievedCarParkFlow.first()
        assertEquals(carParkToFind, retrievedCarPark)
    }

    @Test
    fun `refresh throws error for UI to catch`() = runTest {
        val fakeDao = FakeCarParkDao()
        fakeApiService.setShouldReturnNetworkError(true)
        val repo = CachingCarParkRepository(fakeDao, fakeApiService, context)

        try {
            repo.refresh()
            fail("Expected an exception during refresh, but none was thrown")
        } catch (e: Exception) {
            assertTrue(e is IOException)
        }
    }

    @Test
    fun `refresh inserts new car parks`() = runTest {
        val fakeDao = FakeCarParkDao()
        val repo = CachingCarParkRepository(fakeDao, fakeApiService, context)

        repo.refresh()

        assertEquals(fakeApiService.getCarParks().results.size, fakeDao.carParks!!.size)
    }
}
