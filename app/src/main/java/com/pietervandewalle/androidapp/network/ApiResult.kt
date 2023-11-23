package com.pietervandewalle.androidapp.network

import kotlinx.serialization.Serializable

@Serializable
data class ApiResult<T> (
    val total_count: Int,
    val results: List<T>,
)
