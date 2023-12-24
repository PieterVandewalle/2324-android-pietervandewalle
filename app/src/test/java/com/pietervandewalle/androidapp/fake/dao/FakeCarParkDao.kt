package com.pietervandewalle.androidapp.fake.dao

import com.pietervandewalle.androidapp.data.database.dao.CarParkDao
import com.pietervandewalle.androidapp.data.database.entity.DbCarPark
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class FakeCarParkDao(initialCarParks: List<DbCarPark>? = emptyList()) : CarParkDao {

    private var _carParks: MutableMap<Int, DbCarPark>? = null

    var carParks: List<DbCarPark>?
        get() = _carParks?.values?.toList()?.sortedBy { it.name }
        set(newCarParks) {
            _carParks = newCarParks?.associateBy { it.id }?.toMutableMap()
        }

    init {
        carParks = initialCarParks
    }

    override suspend fun insert(carPark: DbCarPark) {
        val uniqueId = (carParks?.size ?: 0) + 1
        var carParkToInsert = carPark
        if (carParkToInsert.id == 0) {
            carParkToInsert = carParkToInsert.copy(id = uniqueId)
        }
        carParks = carParks?.plus(carParkToInsert)
    }

    override fun getAll(): Flow<List<DbCarPark>> = flow {
        emit(carParks ?: emptyList())
    }

    override fun getById(id: Int): Flow<DbCarPark> = flow {
        emit(carParks?.firstOrNull { it.id == id } ?: throw Exception("CarPark not found"))
    }
}
