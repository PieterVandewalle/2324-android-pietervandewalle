package com.pietervandewalle.androidapp.model

import java.time.LocalDate

/**
 * Represents an article with essential information.
 *
 * @property id The unique identifier for the article. Default is 0.
 * @property title The title of the article.
 * @property date The publication date of the article.
 * @property readMoreUrl The URL for reading the full article.
 * @property content The content of the article (optional). Default is null.
 * @property imageUrl The URL of the article's image (optional). Default is null.
 */
data class Article(
    val id: Int = 0,
    val title: String,
    val date: LocalDate,
    val readMoreUrl: String,
    val content: String? = null,
    val imageUrl: String? = null,
)
