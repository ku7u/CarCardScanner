package com.olequacircuits.carcardscanner.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "cars")
data class Car(

    @PrimaryKey
    val carId: String,

    val railroad: String,

    val carNumber: String,

    val aar: String,

    val color: String
)