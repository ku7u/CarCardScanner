package com.olequacircuits.carcardscanner.database

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "locations",
    indices = [
        Index(value = ["name"], unique = true)
    ]
)
data class Location(

    @PrimaryKey
    val locationId: Int,

    val name: String
)