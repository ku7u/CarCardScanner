package com.olequacircuits.carcardscanner

import androidx.lifecycle.ViewModel

class ScannerViewModel : ViewModel() {

    var currentLocationId: Int = 0

    var currentLocationName: String = ""

    val scannedCars = mutableListOf<String>()

}

