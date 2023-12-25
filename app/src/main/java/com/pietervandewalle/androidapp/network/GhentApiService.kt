package com.pietervandewalle.androidapp.network

import kotlinx.coroutines.flow.flow
import retrofit2.http.GET

/**
 * Interface for interacting with the Ghent API to retrieve various data.
 */
interface GhentApiService {

    /**
     * Retrieves real-time car park data from the Ghent API.
     *
     * @return An [ApiResult] containing car park data.
     */
    @GET("bezetting-parkeergarages-real-time/records?order_by=name&limit=20")
    suspend fun getCarParks(): ApiResult<ApiCarPark>

    /**
     * Retrieves recent news articles from the Ghent API.
     *
     * @return An [ApiResult] containing news articles.
     */
    @GET("recente-nieuwsberichten-van-stadgent/records?order_by=publicatiedatum DESC&limit=20")
    suspend fun getArticles(): ApiResult<ApiArticle>

    /**
     * Retrieves study location data from the Ghent API.
     *
     * @return An [ApiResult] containing study location data.
     */
    @GET("bloklocaties-gent/records?order_by=titel&limit=100")
    suspend fun getStudyLocations(): ApiResult<ApiStudyLocation>
}

/**
 * Converts the result of [getArticles] to a flow.
 *
 * @receiver The [GhentApiService] instance.
 * @return A flow emitting the result of [getArticles].
 */
fun GhentApiService.getArticlesAsFlow() = flow { emit(getArticles()) }

/**
 * Converts the result of [getStudyLocations] to a flow.
 *
 * @receiver The [GhentApiService] instance.
 * @return A flow emitting the result of [getStudyLocations].
 */
fun GhentApiService.getStudyLocationsAsFlow() = flow { emit(getStudyLocations()) }

/**
 * Converts the result of [getCarParks] to a flow.
 *
 * @receiver The [GhentApiService] instance.
 * @return A flow emitting the result of [getCarParks].
 */
fun GhentApiService.getCarParksAsFlow() = flow { emit(getCarParks()) }
