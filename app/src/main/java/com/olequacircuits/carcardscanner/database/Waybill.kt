package com.olequacircuits.carcardscanner.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "waybills")
data class Waybill(

    @PrimaryKey
    val waybillId: Int,

    val carId: String,

    val destinationId: Int,

    val loadStatus: String? = null
)