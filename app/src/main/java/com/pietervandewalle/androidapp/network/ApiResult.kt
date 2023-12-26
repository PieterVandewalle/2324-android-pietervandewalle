package com.pietervandewalle.androidapp.network

import kotlinx.serialization.Serializable

/**
 * Represents a generic API result containing a total count and a list of results of type [T].
 *
 * @property total_count The total count of results.
 * @property results The list of results of type [T].
 * @param T The type of results contained in the API result.
 */
@Serializable
data class ApiResult<T> (
    val total_count: Int,
    val results: List<T>,
)
