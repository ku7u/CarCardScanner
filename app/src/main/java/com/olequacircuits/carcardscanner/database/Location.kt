package com.olequacircuits.carcardscanner.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "locations")
data class Location(
    @PrimaryKey
    val id: Int,

    val name: String
)