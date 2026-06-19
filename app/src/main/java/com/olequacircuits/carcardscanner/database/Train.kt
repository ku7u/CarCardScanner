package com.olequacircuits.carcardscanner.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "trains")
data class Train(

    @PrimaryKey
    val trainId: Int,

    val name: String
)