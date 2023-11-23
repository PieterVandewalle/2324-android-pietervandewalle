package com.pietervandewalle.androidapp.model

import java.time.LocalDate

data class Article(
    val title: String,
    val date: LocalDate,
    val readMoreUrl: String,
)
