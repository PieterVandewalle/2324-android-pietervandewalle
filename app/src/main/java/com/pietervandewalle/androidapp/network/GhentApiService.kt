package com.pietervandewalle.androidapp.network

import retrofit2.http.GET
import retrofit2.http.Query

interface GhentApiService {
    @GET("bezetting-parkeergarages-real-time/records?order_by=name&limit=20")
    suspend fun getCarParks(): ApiResult<ApiCarPark>

    @GET("recente-nieuwsberichten-van-stadgent/records?order_by=publicatiedatum DESC&limit=20")
    suspend fun getArticles(): ApiResult<ApiArticle>

    @GET("bloklocaties-gent/records?order_by=titel&limit=100")
    suspend fun getStudyLocations(@Query("where")where: String? = null): ApiResult<ApiStudyLocation>
}
