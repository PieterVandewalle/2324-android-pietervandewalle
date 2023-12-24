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

@Database(entities = [DbArticle::class, DbCarPark::class, DbStudyLocation::class], version = 1)
@TypeConverters(Converters::class)
abstract class MyRoomDatabase : RoomDatabase() {
    abstract fun articleDao(): ArticleDao
    abstract fun studyLocationDao(): StudyLocationDao
    abstract fun carParkDao(): CarParkDao
}
