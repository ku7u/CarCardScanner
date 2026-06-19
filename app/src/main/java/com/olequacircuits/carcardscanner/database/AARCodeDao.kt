package com.olequacircuits.carcardscanner.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface AARCodeDao {

    @Query("SELECT * FROM aarcodes ORDER BY aar")
    suspend fun getAll(): List<AARCode>

    @Query("SELECT description FROM aarcodes WHERE aar = :aar")
    suspend fun getDescription(aar: String): String?

    @Insert
    suspend fun insert(aarCode: AARCode)
}