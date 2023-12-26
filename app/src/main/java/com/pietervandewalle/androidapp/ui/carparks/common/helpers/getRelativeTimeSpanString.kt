package com.pietervandewalle.androidapp.ui.carparks.common.helpers

import android.text.format.DateUtils
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneOffset

/**
 * Gets a human-readable relative time span string based on the provided [LocalDateTime].
 *
 * @param localDateTime The [LocalDateTime] to calculate the relative time span for.
 * @return A human-readable relative time span string.
 */
fun getRelativeTimeSpanString(localDateTime: LocalDateTime): String {
    val lastUpdateMillis = localDateTime.toInstant(ZoneOffset.UTC).toEpochMilli()
    val currentMillis = Instant.now().toEpochMilli()

    // Format elapsed time using DateUtils.getRelativeTimeSpanString
    return DateUtils.getRelativeTimeSpanString(
        lastUpdateMillis,
        currentMillis,
        DateUtils.MINUTE_IN_MILLIS,
    ).toString()
}
