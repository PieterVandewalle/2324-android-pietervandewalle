package com.pietervandewalle.androidapp.ui.articles.common.helpers

import java.time.LocalDate
import java.time.format.DateTimeFormatter

val dateFormatter: DateTimeFormatter = DateTimeFormatter.ofPattern("dd MMMM yyyy")
fun formatArticleDate(date: LocalDate) : String {
    return dateFormatter.format(date)
}
