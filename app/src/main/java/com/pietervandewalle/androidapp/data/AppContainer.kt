package com.pietervandewalle.androidapp.data

import android.content.Context
import androidx.room.Room
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import com.pietervandewalle.androidapp.data.database.MyRoomDatabase
import com.pietervandewalle.androidapp.data.database.dao.ArticleDao
import com.pietervandewalle.androidapp.data.database.dao.CarParkDao
import com.pietervandewalle.androidapp.data.database.dao.StudyLocationDao
import com.pietervandewalle.androidapp.data.repo.ArticleRepository
import com.pietervandewalle.androidapp.data.repo.CachingArticleRepository
import com.pietervandewalle.androidapp.data.repo.CachingCarParkRepository
import com.pietervandewalle.androidapp.data.repo.CachingStudyLocationRepository
import com.pietervandewalle.androidapp.data.repo.CarParkRepository
import com.pietervandewalle.androidapp.data.repo.StudyLocationRepository
import com.pietervandewalle.androidapp.network.GhentApiService
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import retrofit2.Retrofit

/**
 * This interface defines the dependencies that can be accessed through the [AppContainer].
 */
interface AppContainer {
    /**
     * Provides access to the [CarParkRepository] dependency.
     */
    val carParkRepository: CarParkRepository

    /**
     * Provides access to the [ArticleRepository] dependency.
     */
    val articleRepository: ArticleRepository

    /**
     * Provides access to the [StudyLocationRepository] dependency.
     */
    val studyLocationRepository: StudyLocationRepository
}

/**
 * The default implementation of the [AppContainer] interface.
 *
 * @param applicationContext The Android application context.
 */
class DefaultAppContainer(private val applicationContext: Context) : AppContainer {

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

    private val myRoomDb: MyRoomDatabase by lazy {
        Room.databaseBuilder(applicationContext, MyRoomDatabase::class.java, "myapp_db").build()
    }

    private val articleDao: ArticleDao by lazy {
        myRoomDb.articleDao()
    }

    private val studyLocationDao: StudyLocationDao by lazy {
        myRoomDb.studyLocationDao()
    }

    private val carParkDao: CarParkDao by lazy {
        myRoomDb.carParkDao()
    }

    /**
     * Provides access to the [ArticleRepository] dependency.
     */
    override val articleRepository: ArticleRepository by lazy {
        CachingArticleRepository(articleDao = articleDao, ghentApiService = retrofitService, context = applicationContext)
    }

    /**
     * Provides access to the [StudyLocationRepository] dependency.
     */
    override val studyLocationRepository: StudyLocationRepository by lazy {
        CachingStudyLocationRepository(studyLocationDao = studyLocationDao, ghentApiService = retrofitService)
    }

    /**
     * Provides access to the [CarParkRepository] dependency.
     */
    override val carParkRepository: CarParkRepository by lazy {
        CachingCarParkRepository(carParkDao = carParkDao, ghentApiService = retrofitService, context = applicationContext)
    }
}
