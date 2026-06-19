package com.olequacircuits.carcardscanner.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "aarcodes")
data class AARCode(

    @PrimaryKey
    val aar: String,

    val description: String
)