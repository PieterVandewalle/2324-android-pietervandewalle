package com.pietervandewalle.androidapp.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.pietervandewalle.androidapp.data.database.dao.ArticleDao
import com.pietervandewalle.androidapp.data.database.dao.CarParkDao
import com.pietervandewalle.androidapp.data.database.dao.StudyLocationDao
import com.pietervandewalle.androidapp.data.database.entity.DbArticle
import com.pietervandewalle.androidapp.data.database.entity.DbCarPark
import com.pietervandewalle.androidapp.data.database.entity.DbStudyLocation

/**
 * This class represents the Room database for storing articles, car parks, and study locations.
 *
 * @property articleDao The Data Access Object (DAO) for articles.
 * @property studyLocationDao The Data Access Object (DAO) for study locations.
 * @property carParkDao The Data Access Object (DAO) for car parks.
 */
@Database(entities = [DbArticle::class, DbCarPark::class, DbStudyLocation::class], version = 1)
@TypeConverters(Converters::class)
abstract class MyRoomDatabase : RoomDatabase() {
    /**
     * Provides access to the [ArticleDao] for interacting with article data.
     *
     * @return The [ArticleDao] instance.
     */
    abstract fun articleDao(): ArticleDao

    /**
     * Provides access to the [StudyLocationDao] for interacting with study location data.
     *
     * @return The [StudyLocationDao] instance.
     */
    abstract fun studyLocationDao(): StudyLocationDao

    /**
     * Provides access to the [CarParkDao] for interacting with car park data.
     *
     * @return The [CarParkDao] instance.
     */
    abstract fun carParkDao(): CarParkDao
}
