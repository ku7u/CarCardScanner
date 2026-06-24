package com.olequacircuits.carcardscanner.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface WaybillDao {

    @Query("SELECT * FROM waybills ORDER BY waybillId")
    suspend fun getAll(): List<Waybill>

    @Query("SELECT * FROM waybills WHERE waybillId = :id")
    suspend fun getById(id: Int): Waybill?

    @Query("SELECT COUNT(*) FROM waybills")
    suspend fun getCount(): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(waybill: Waybill)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(waybills: List<Waybill>)
}