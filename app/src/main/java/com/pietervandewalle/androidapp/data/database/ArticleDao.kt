package com.pietervandewalle.androidapp.data.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface ArticleDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(article: DbArticle)

    @Query("SELECT * from articles ORDER BY date DESC, title ASC")
    fun getAll(): Flow<List<DbArticle>>

    @Query("SELECT * from articles WHERE id = :id")
    fun getById(id: Int): Flow<DbArticle>
}
