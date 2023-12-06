package com.pietervandewalle.androidapp.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

@Database(entities = [DbArticle::class], version = 1)
@TypeConverters(Converters::class)
abstract class MyRoomDatabase : RoomDatabase() {
    abstract fun articleDao(): ArticleDao
}
