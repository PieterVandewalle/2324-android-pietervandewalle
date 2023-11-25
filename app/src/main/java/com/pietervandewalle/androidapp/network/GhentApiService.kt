package com.pietervandewalle.androidapp.network

import retrofit2.http.GET

interface GhentApiService {
    @GET("bezetting-parkeergarages-real-time/records?limit=20")
    suspend fun getCarParks(): ApiResult<ApiCarPark>

    @GET("recente-nieuwsberichten-van-stadgent/records?limit=20")
    suspend fun getArticles(): ApiResult<ApiArticle>
}
