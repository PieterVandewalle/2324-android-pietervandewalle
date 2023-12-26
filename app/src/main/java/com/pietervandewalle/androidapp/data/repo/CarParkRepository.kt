package com.pietervandewalle.androidapp.data.repo

import android.content.Context
import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.pietervandewalle.androidapp.data.database.dao.CarParkDao
import com.pietervandewalle.androidapp.data.database.entity.asDbCarPark
import com.pietervandewalle.androidapp.data.database.entity.asDomainCarPark
import com.pietervandewalle.androidapp.data.database.entity.asDomainCarParks
import com.pietervandewalle.androidapp.model.CarPark
import com.pietervandewalle.androidapp.network.GhentApiService
import com.pietervandewalle.androidapp.network.asDomainObjects
import com.pietervandewalle.androidapp.network.getCarParksAsFlow
import com.pietervandewalle.androidapp.workers.CarParksNotificationWorker
import com.pietervandewalle.androidapp.workers.CarParksRefreshWorker
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import java.util.concurrent.TimeUnit

/**
 * Interface for managing car parks.
 */
interface CarParkRepository {
    /**
     * Retrieves all car parks from the repository as a Flow of lists.
     *
     * @return A Flow emitting a list of car parks.
     */
    fun getAll(): Flow<List<CarPark>>

    /**
     * Retrieves a car park by its unique identifier from the repository as a Flow.
     *
     * @param id The unique identifier of the car park to retrieve.
     * @return A Flow emitting the requested car park.
     */
    fun getById(id: Int): Flow<CarPark>

    /**
     * Inserts a car park into the repository.
     *
     * @param carPark The car park to insert.
     */
    suspend fun insert(carPark: CarPark)

    /**
     * Refreshes the repository, typically by fetching updated data from a remote source.
     */
    suspend fun refresh()
}

/**
 * Implementation of [CarParkRepository] that caches car parks using a local database
 * (represented by [carParkDao]) and fetches data from a remote source (represented by [ghentApiService]).
 *
 * @param carParkDao The data access object for the local database.
 * @param ghentApiService The API service for fetching remote data.
 * @param context The Android application context.
 */
class CachingCarParkRepository(private val carParkDao: CarParkDao, private val ghentApiService: GhentApiService, context: Context) : CarParkRepository {
    override fun getAll(): Flow<List<CarPark>> {
        return carParkDao.getAll().map { it.asDomainCarParks() }.onStart {
            startWorkers()
        }.onEach {
            if (it.isEmpty()) {
                refresh()
            }
        }
    }

    override fun getById(id: Int): Flow<CarPark> {
        return carParkDao.getById(id).map { it.asDomainCarPark() }
    }

    override suspend fun insert(carPark: CarPark) {
        carParkDao.insert(carPark.asDbCarPark())
    }

    override suspend fun refresh() {
        ghentApiService.getCarParksAsFlow().collect {
            for (carPark in it.results.asDomainObjects()) {
                insert(carPark)
            }
        }
    }

    private val workManager = WorkManager.getInstance(context)

    private fun startWorkers() {
        val constraints = Constraints.Builder().setRequiredNetworkType(NetworkType.CONNECTED).setRequiresBatteryNotLow(true).build()

        // Notification worker (every hour)
        val notifyAboutCarParksPeriodicallyRequest =
            PeriodicWorkRequestBuilder<CarParksNotificationWorker>(
                1, // repeatInterval (the period cycle)
                TimeUnit.HOURS,
                15, // flexInterval
                TimeUnit.MINUTES,
            )
                .setConstraints(constraints)
                .build()

        workManager.enqueueUniquePeriodicWork(
            "notifyAboutCarParksPeriodically",
            ExistingPeriodicWorkPolicy.KEEP,
            notifyAboutCarParksPeriodicallyRequest,
        )

        // Refresh every 15 minutes worker
        val refreshCarParksPeriodicallyRequest =
            PeriodicWorkRequestBuilder<CarParksRefreshWorker>(
                15,
                TimeUnit.MINUTES,
            )
                .setConstraints(constraints)
                .build()

        workManager.enqueueUniquePeriodicWork(
            "refreshCarParksPeriodically",
            ExistingPeriodicWorkPolicy.KEEP,
            refreshCarParksPeriodicallyRequest,
        )
    }
}
