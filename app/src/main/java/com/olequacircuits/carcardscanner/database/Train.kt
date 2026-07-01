package com.olequacircuits.carcardscanner.database

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "trains",
    indices = [
        Index(value = ["name"], unique = true)
    ]
)
data class Train(

    @PrimaryKey(autoGenerate = true)
    val trainId: Int = 0,

    val name: String
)