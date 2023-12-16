package com.pietervandewalle.androidapp.data.repo

import android.content.Context
import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.pietervandewalle.androidapp.data.database.CarParkDao
import com.pietervandewalle.androidapp.data.database.asDbCarPark
import com.pietervandewalle.androidapp.data.database.asDomainCarPark
import com.pietervandewalle.androidapp.data.database.asDomainCarParks
import com.pietervandewalle.androidapp.model.CarPark
import com.pietervandewalle.androidapp.network.GhentApiService
import com.pietervandewalle.androidapp.network.asDomainObjects
import com.pietervandewalle.androidapp.network.getCarParksAsFlow
import com.pietervandewalle.androidapp.workers.CarParksNotificationWorker
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import java.util.concurrent.TimeUnit

interface CarParkRepository {
    suspend fun insert(carPark: CarPark)
    fun getAll(): Flow<List<CarPark>>
    fun getById(id: Int): Flow<CarPark>
    suspend fun refresh()
}

class CachingCarParkRepository(private val carParkDao: CarParkDao, private val ghentApiService: GhentApiService, context: Context) : CarParkRepository {
    override suspend fun insert(carPark: CarPark) {
        carParkDao.insert(carPark.asDbCarPark())
    }

    override fun getAll(): Flow<List<CarPark>> {
        return carParkDao.getAll().map { it.asDomainCarParks() }.onEach {
            if (it.isEmpty()) {
                refresh()
            }
        }
    }

    override fun getById(id: Int): Flow<CarPark> {
        return carParkDao.getById(id).map { it.asDomainCarPark() }
    }

    private val workManager = WorkManager.getInstance(context)

    override suspend fun refresh() {
        val constraints = Constraints.Builder().setRequiredNetworkType(NetworkType.CONNECTED).setRequiresBatteryNotLow(true).build()
        val refreshCarParksPeriodicallyRequest =
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
            refreshCarParksPeriodicallyRequest,
        )

        ghentApiService.getCarParksAsFlow().collect {
            for (carPark in it.results.asDomainObjects()) {
                insert(carPark)
            }
        }
    }
}
