package com.pietervandewalle.androidapp.data.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.pietervandewalle.androidapp.data.database.entity.DbStudyLocation
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object (DAO) for managing study locations in the database.
 * This interface provides methods for inserting, querying, and retrieving study locations.
 */
@Dao
interface StudyLocationDao {
    /**
     * Insert a new study location into the database. If there is a conflict, it will be replaced.
     *
     * @param studyLocation The [DbStudyLocation] to be inserted or replaced.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(studyLocation: DbStudyLocation)

    /**
     * Retrieve all study locations from the database, ordered by title.
     *
     * @return A [Flow] of [List] of [DbStudyLocation], representing the list of study locations sorted by title.
     */
    @Query("SELECT * from studyLocations ORDER BY title")
    fun getAll(): Flow<List<DbStudyLocation>>

    /**
     * Retrieve study locations from the database that match the provided search term in title or address,
     * ordered by title.
     *
     * @param searchTerm The search term to filter study locations by title or address.
     * @return A [Flow] of [List] of [DbStudyLocation], representing the list of study locations matching the search term.
     */
    @Query("SELECT * FROM studyLocations WHERE title LIKE '%' || :searchTerm || '%' OR address LIKE '%' || :searchTerm || '%' ORDER BY title")
    fun getAllBySearchTerm(searchTerm: String): Flow<List<DbStudyLocation>>

    /**
     * Retrieve a study location from the database by its ID.
     *
     * @param id The ID of the study location to retrieve.
     * @return A [Flow] of [DbStudyLocation] representing the study location with the specified ID.
     */
    @Query("SELECT * from studyLocations WHERE id = :id")
    fun getById(id: Int): Flow<DbStudyLocation>
}
