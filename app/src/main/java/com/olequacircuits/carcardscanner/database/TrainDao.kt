package com.olequacircuits.carcardscanner.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface TrainDao {
    @Query("SELECT * FROM trains ORDER BY trainId")
    suspend fun getAll(): List<Train>

    @Query("SELECT COUNT(*) FROM trains")
    suspend fun getCount(): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(train: Train)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(trains: List<Train>)
}