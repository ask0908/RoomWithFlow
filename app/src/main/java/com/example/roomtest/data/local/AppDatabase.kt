package com.example.roomtest.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.roomtest.data.GenreConverters
import com.example.roomtest.model.Movie

@Database(entities = [Movie::class], version = 1, exportSchema = false)
@TypeConverters(GenreConverters::class)
abstract class AppDatabase: RoomDatabase() {
    abstract fun movieDao(): MovieDao
}