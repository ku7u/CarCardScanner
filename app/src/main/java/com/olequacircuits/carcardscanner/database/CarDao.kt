package com.olequacircuits.carcardscanner.database

import androidx.room.*

@Dao
interface CarDao {

    @Query("SELECT * FROM cars ORDER BY roadname, roadnum")
    suspend fun getAll(): List<Car>

    @Query("""
        SELECT * FROM cars
        WHERE roadname = :roadname
          AND roadnum = :roadnum
    """)
    suspend fun getByRoadAndNumber(
        roadname: String,
        roadnum: String
    ): Car?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(car: Car)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(cars: List<Car>)

    @Update
    suspend fun update(car: Car)

    @Delete
    suspend fun delete(car: Car)

    @Query("SELECT COUNT(*) FROM cars")
    suspend fun getCount(): Int
}