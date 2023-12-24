package com.pietervandewalle.androidapp.fake

import com.pietervandewalle.androidapp.network.ApiArticle
import com.pietervandewalle.androidapp.network.ApiCarPark
import com.pietervandewalle.androidapp.network.ApiResult
import com.pietervandewalle.androidapp.network.ApiStudyLocation
import com.pietervandewalle.androidapp.network.GhentApiService
import java.io.IOException

class FakeGhentApiService(private val carParks: List<ApiCarPark>, private val articles: List<ApiArticle>, private val studyLocations: List<ApiStudyLocation>) : GhentApiService {
    private var shouldReturnNetworkError = false

    fun setShouldReturnNetworkError(value: Boolean) {
        shouldReturnNetworkError = value
    }

    override suspend fun getCarParks(): ApiResult<ApiCarPark> {
        if (shouldReturnNetworkError) {
            throw IOException("Network error simulated by FakeGhentApiService")
        }
        return ApiResult(results = carParks, total_count = carParks.size)
    }

    override suspend fun getArticles(): ApiResult<ApiArticle> {
        if (shouldReturnNetworkError) {
            throw IOException("Network error simulated by FakeGhentApiService")
        }
        return ApiResult(results = articles, total_count = articles.size)
    }

    override suspend fun getStudyLocations(): ApiResult<ApiStudyLocation> {
        if (shouldReturnNetworkError) {
            throw IOException("Network error simulated by FakeGhentApiService")
        }
        return ApiResult(results = studyLocations, total_count = studyLocations.size)
    }
}
