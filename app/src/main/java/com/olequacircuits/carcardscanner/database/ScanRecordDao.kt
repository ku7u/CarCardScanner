package com.olequacircuits.carcardscanner.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface ScanRecordDao {

    @Insert
    suspend fun insert(scanRecord: ScanRecord)

    @Query("SELECT * FROM scanrecords WHERE trainId = :trainId ORDER BY id")
    suspend fun getByTrain(trainId: Int): List<ScanRecord>

    @Query("SELECT * FROM scanrecords WHERE locationId = :locationId ORDER BY id")
    suspend fun getByLocation(locationId: Int): List<ScanRecord>

    @Query("""
        SELECT * FROM scanrecords
        WHERE trainId = :trainId
        AND locationId = :locationId
        ORDER BY id
    """)
    suspend fun getTrainLocationScans(
        trainId: Int,
        locationId: Int
    ): List<ScanRecord>

    @Query("DELETE FROM scanrecords WHERE trainId = :trainId")
    suspend fun deleteTrainSession(trainId: Int)
}