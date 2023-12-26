package com.pietervandewalle.androidapp.ui.articles.common.helpers

import java.time.LocalDate
import java.time.format.DateTimeFormatter

/**
 * A [DateTimeFormatter] for formatting dates in the "dd MMMM yyyy" pattern.
 */
val dateFormatter: DateTimeFormatter = DateTimeFormatter.ofPattern("dd MMMM yyyy")

/**
 * Formats the given [date] using the [dateFormatter].
 *
 * @param date The date to be formatted.
 * @return A formatted date string in the "dd MMMM yyyy" pattern.
 */
fun formatArticleDate(date: LocalDate): String {
    return dateFormatter.format(date)
}
