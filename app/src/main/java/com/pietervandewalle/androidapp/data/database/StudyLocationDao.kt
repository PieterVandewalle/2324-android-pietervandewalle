package com.pietervandewalle.androidapp.data.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface StudyLocationDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(studyLocation: DbStudyLocation)

    @Query("SELECT * from studyLocations ORDER BY title")
    fun getAll(): Flow<List<DbStudyLocation>>

    @Query("SELECT * FROM studyLocations WHERE title LIKE '%' || :searchTerm || '%' OR address LIKE '%' || :searchTerm || '%' ORDER BY title")
    fun getAllBySearchTerm(searchTerm: String): Flow<List<DbStudyLocation>>

    @Query("SELECT * from studyLocations WHERE id = :id")
    fun getById(id: Int): Flow<DbStudyLocation>
}
