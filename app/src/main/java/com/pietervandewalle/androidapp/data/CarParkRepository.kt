package com.pietervandewalle.androidapp.data

import com.pietervandewalle.androidapp.model.CarPark
import com.pietervandewalle.androidapp.network.GhentApiService
import com.pietervandewalle.androidapp.network.asDomainObjects
import java.io.IOException

interface CarParkRepository {
    suspend fun getCarParks(): List<CarPark>
    suspend fun getCarParkByName(name: String): CarPark
}
class ApiCarParkRepository(private val ghentApiService: GhentApiService) : CarParkRepository {
    override suspend fun getCarParks(): List<CarPark> {
        return ghentApiService.getCarParks().results.asDomainObjects().sortedBy { carPark -> carPark.name.lowercase() }
    }
    override suspend fun getCarParkByName(name: String): CarPark {
        return ghentApiService.getCarParks().results.asDomainObjects().firstOrNull { carPark -> carPark.name == name } ?: throw IOException()
    }
}
