package com.pietervandewalle.androidapp.data

import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import com.pietervandewalle.androidapp.network.GhentApiService
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import retrofit2.Retrofit

interface AppContainer {
    val carParkRepository: CarParkRepository
}

// container that takes care of dependencies
class DefaultAppContainer() : AppContainer {

    private val baseUrl = "https://data.stad.gent/api/explore/v2.1/catalog/datasets/"

    private val configuredJson = Json {
        ignoreUnknownKeys = true
    }
    private val retrofit = Retrofit.Builder()
        .addConverterFactory(
            configuredJson.asConverterFactory("application/json".toMediaType()),
        )
        .baseUrl(baseUrl)
        .build()

    private val retrofitService: GhentApiService by lazy {
        retrofit.create(GhentApiService::class.java)
    }

    override val carParkRepository: CarParkRepository by lazy {
        ApiCarParkRepository(retrofitService)
    }
}
