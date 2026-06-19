package com.olequacircuits.carcardscanner.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "scanrecords")
data class ScanRecord(

    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    val carId: String,

    val locationId: Int,

    val destinationId: Int,

    val trainId: Int,

    val timestamp: Long
)