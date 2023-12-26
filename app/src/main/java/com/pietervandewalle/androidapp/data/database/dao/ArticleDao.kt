package com.pietervandewalle.androidapp.data.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.pietervandewalle.androidapp.data.database.entity.DbArticle
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object (DAO) for managing articles in the database.
 * This interface provides methods for inserting, querying, and retrieving articles.
 */
@Dao
interface ArticleDao {
    /**
     * Insert a new article into the database. If there is a conflict, it will be ignored.
     *
     * @param article The [DbArticle] to be inserted.
     */
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(article: DbArticle)

    /**
     * Retrieve all articles from the database, ordered by date in descending order and title in ascending order.
     *
     * @return A [Flow] of [List] of [DbArticle], representing the list of articles in the specified order.
     */
    @Query("SELECT * from articles ORDER BY date DESC, title ASC")
    fun getAll(): Flow<List<DbArticle>>

    /**
     * Retrieve an article from the database by its ID.
     *
     * @param id The ID of the article to retrieve.
     * @return A [Flow] of [DbArticle] representing the article with the specified ID.
     */
    @Query("SELECT * from articles WHERE id = :id")
    fun getById(id: Int): Flow<DbArticle>
}
