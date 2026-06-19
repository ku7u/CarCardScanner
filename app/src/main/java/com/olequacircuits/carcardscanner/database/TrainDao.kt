package com.olequacircuits.carcardscanner.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface TrainDao {

    @Query("SELECT * FROM trains ORDER BY trainId")
    suspend fun getAll(): List<Train>

    @Insert
    suspend fun insert(train: Train)
}