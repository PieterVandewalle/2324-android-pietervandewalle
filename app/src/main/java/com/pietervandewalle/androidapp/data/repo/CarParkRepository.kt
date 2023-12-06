package com.pietervandewalle.androidapp.data.repo

import com.pietervandewalle.androidapp.data.database.CarParkDao
import com.pietervandewalle.androidapp.data.database.asDbCarPark
import com.pietervandewalle.androidapp.data.database.asDomainCarPark
import com.pietervandewalle.androidapp.data.database.asDomainCarParks
import com.pietervandewalle.androidapp.model.CarPark
import com.pietervandewalle.androidapp.network.GhentApiService
import com.pietervandewalle.androidapp.network.asDomainObjects
import com.pietervandewalle.androidapp.network.getCarParksAsFlow
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach

interface CarParkRepository {
    suspend fun insert(carPark: CarPark)
    fun getAll(): Flow<List<CarPark>>
    fun getById(id: Int): Flow<CarPark>
    suspend fun refresh()
}

class CachingCarParkRepository(private val carParkDao: CarParkDao, private val ghentApiService: GhentApiService) : CarParkRepository {
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

    override suspend fun refresh() {
        ghentApiService.getCarParksAsFlow().collect {
            for (carPark in it.results.asDomainObjects()) {
                insert(carPark)
            }
        }
    }
}
