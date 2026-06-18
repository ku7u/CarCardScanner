package com.olequacircuits.carcardscanner.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface LocationDao {

    @Query("SELECT * FROM locations ORDER BY id")
    suspend fun getAll(): List<Location>


    @Insert
    suspend fun insert(location: Location)
}