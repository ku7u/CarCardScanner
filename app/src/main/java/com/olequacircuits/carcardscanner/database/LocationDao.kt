package com.olequacircuits.carcardscanner.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface LocationDao {
    @Query("SELECT * FROM locations ORDER BY locationId")
    suspend fun getAll(): List<Location>

    @Query("SELECT COUNT(*) FROM locations")
    suspend fun getCount(): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(locations: List<Location>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(location: Location)
}