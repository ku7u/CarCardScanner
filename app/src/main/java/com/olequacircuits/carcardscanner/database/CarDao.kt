package com.olequacircuits.carcardscanner.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import androidx.room.Delete

@Dao
interface CarDao {

    @Query("SELECT * FROM cars ORDER BY carId")
    suspend fun getAll(): List<Car>

    @Query("SELECT * FROM cars WHERE carId = :id")
    suspend fun getById(id: String): Car?

    @Insert
    suspend fun insert(car: Car)

    @Update
    suspend fun update(car: Car)

    @Delete
    suspend fun delete(car: Car)
}