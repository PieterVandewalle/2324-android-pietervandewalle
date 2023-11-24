package com.pietervandewalle.androidapp.data

import com.pietervandewalle.androidapp.model.CarPark
import com.pietervandewalle.androidapp.network.GhentApiService
import com.pietervandewalle.androidapp.network.asDomainObjects

interface CarParkRepository {
    suspend fun getCarParks(): List<CarPark>
}
class ApiCarParkRepository(private val ghentApiService: GhentApiService) : CarParkRepository {
    override suspend fun getCarParks(): List<CarPark> {
        return ghentApiService.getCarParks().results.asDomainObjects().sortedBy { carPark -> carPark.name.lowercase() }
    }
}
