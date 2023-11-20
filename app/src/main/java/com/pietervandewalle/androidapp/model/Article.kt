package com.pietervandewalle.androidapp.model

import java.util.Date

data class Article(
    val author: String,
    val title: String,
    val content: String,
    val date: Date,
    val imageUrl: String,
    val readMoreUrl: String,
    val tags: List<String>,
)
