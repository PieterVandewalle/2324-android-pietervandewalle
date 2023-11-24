package com.pietervandewalle.androidapp.model

import java.time.LocalDate

data class Article(
    val title: String,
    val date: LocalDate,
    val readMoreUrl: String,
    val content: String? = null,
    val imageUrl: String? = null,
)
