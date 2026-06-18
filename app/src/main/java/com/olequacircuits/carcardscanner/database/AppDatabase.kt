package com.olequacircuits.carcardscanner.database

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [Location::class],
    version = 1
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun locationDao(): LocationDao
}