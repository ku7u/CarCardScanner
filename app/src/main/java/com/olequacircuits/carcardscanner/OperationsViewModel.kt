package com.olequacircuits.carcardscanner

import androidx.lifecycle.ViewModel

class OperationsViewModel : ViewModel() {

    // Current train being built
    var activeTrainId: Int? = null
    var activeTrainName: String? = null

    // Current scan session
    var scanSessionActive: Boolean = false

    // Current physical location where scanning is happening
    var currentLocationId: Int = 0
    var currentLocationName: String = ""

    // Cars scanned in this session
    val scannedCars = mutableListOf<String>()

}