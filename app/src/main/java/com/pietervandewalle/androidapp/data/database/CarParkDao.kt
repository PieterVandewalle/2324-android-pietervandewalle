package com.pietervandewalle.androidapp.data.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface CarParkDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(carPark: DbCarPark)

    @Query("SELECT * from carParks ORDER BY name")
    fun getAll(): Flow<List<DbCarPark>>

    @Query("SELECT * from carParks WHERE id = :id")
    fun getById(id: Int): Flow<DbCarPark>
}
