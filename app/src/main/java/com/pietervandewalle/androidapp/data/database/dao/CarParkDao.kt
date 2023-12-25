package com.pietervandewalle.androidapp.data.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.pietervandewalle.androidapp.data.database.entity.DbCarPark
import kotlinx.coroutines.flow.Flow

@Dao
interface CarParkDao {
    /**
     * Insert a new car park into the database. If there is a conflict, it will be replaced.
     *
     * @param carPark The [DbCarPark] to be inserted or replaced.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(carPark: DbCarPark)

    /**
     * Retrieve all car parks from the database, ordered by name.
     *
     * @return A [Flow] of [List] of [DbCarPark], representing the list of car parks sorted by name.
     */
    @Query("SELECT * from carParks ORDER BY name")
    fun getAll(): Flow<List<DbCarPark>>

    /**
     * Retrieve a car park from the database by its ID.
     *
     * @param id The ID of the car park to retrieve.
     * @return A [Flow] of [DbCarPark] representing the car park with the specified ID.
     */
    @Query("SELECT * from carParks WHERE id = :id")
    fun getById(id: Int): Flow<DbCarPark>
}
