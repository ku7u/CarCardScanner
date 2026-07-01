package com.olequacircuits.carcardscanner.database

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "cars",
    indices = [
        Index(
            value = ["roadname", "roadnum"],
            unique = true
        )
    ]
)
data class Car(

    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    val roadname: String,

    val roadnum: String,

    val aarcode: String,

    val length: String,

    val color: String
)