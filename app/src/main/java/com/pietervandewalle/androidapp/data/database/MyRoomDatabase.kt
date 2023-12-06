package com.pietervandewalle.androidapp.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

@Database(entities = [DbArticle::class, DbCarPark::class, DbStudyLocation::class], version = 1)
@TypeConverters(Converters::class)
abstract class MyRoomDatabase : RoomDatabase() {
    abstract fun articleDao(): ArticleDao
    abstract fun studyLocationDao(): StudyLocationDao
    abstract fun carParkDao(): CarParkDao
}
