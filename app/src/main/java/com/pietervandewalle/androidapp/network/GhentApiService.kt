package com.pietervandewalle.androidapp.network

import retrofit2.http.GET

interface GhentApiService {
    @GET("bezetting-parkeergarages-real-time/records?order_by=name&limit=20")
    suspend fun getCarParks(): ApiResult<ApiCarPark>

    @GET("recente-nieuwsberichten-van-stadgent/records?order_by=publicatiedatum DESC&limit=20")
    suspend fun getArticles(): ApiResult<ApiArticle>
}
