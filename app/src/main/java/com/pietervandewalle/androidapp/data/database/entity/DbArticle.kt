package com.pietervandewalle.androidapp.data.database.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import com.pietervandewalle.androidapp.model.Article
import java.time.LocalDate

@Entity(
    tableName = "articles",
    indices = [
        Index(
            value = ["title", "date"],
            unique = true,
        ),
    ],
)
data class DbArticle(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val title: String,
    val date: LocalDate,
    val readMoreUrl: String,
    val content: String? = null,
    val imageUrl: String? = null,
)

fun Article.asDbArticle(): DbArticle =
    DbArticle(id = id, title = title, date = date, readMoreUrl = readMoreUrl, content = content, imageUrl = imageUrl)

fun DbArticle.asDomainArticle(): Article =
    Article(id = id, title = title, date = date, readMoreUrl = readMoreUrl, content = content, imageUrl = imageUrl)
fun List<DbArticle>.asDomainArticles(): List<Article> = map { it.asDomainArticle() }
