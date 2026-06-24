package com.olequacircuits.carcardscanner.database

import android.content.Context
import androidx.room.Room

object DatabaseProvider {

    @Volatile
    private var INSTANCE: AppDatabase? = null

    fun getDatabase(context: Context): AppDatabase {

        return INSTANCE ?: synchronized(this) {

            val instance = Room.databaseBuilder(
                context.applicationContext,
                AppDatabase::class.java,
                "carcardscanner.db"
            )
                // TODO: this should be changed to false for production per chatgpt
                .fallbackToDestructiveMigration(true)
                .build()

            INSTANCE = instance

            instance
        }
    }
}