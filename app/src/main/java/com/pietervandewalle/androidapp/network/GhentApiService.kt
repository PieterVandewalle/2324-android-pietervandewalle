package com.pietervandewalle.androidapp.network

import retrofit2.http.GET

interface GhentApiService {
    @GET("bezetting-parkeergarages-real-time/records")
    suspend fun getCarParks(): ApiResult<ApiCarPark>
}
