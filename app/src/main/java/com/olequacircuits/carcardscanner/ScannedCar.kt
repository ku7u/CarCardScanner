package com.olequacircuits.carcardscanner

data class ScannedCar(
    val carId: String,
    val destinationId: Int,
    val locationId: Int,
    val trainId: Int
)
