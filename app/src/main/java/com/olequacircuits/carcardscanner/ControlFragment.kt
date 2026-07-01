package com.olequacircuits.carcardscanner

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels

import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

import com.olequacircuits.carcardscanner.database.DatabaseProvider
import com.olequacircuits.carcardscanner.database.Train
import com.olequacircuits.carcardscanner.database.AARCode
import com.olequacircuits.carcardscanner.database.Location

// for picker
import android.net.Uri
import android.util.Log
import android.widget.AdapterView
import androidx.activity.result.contract.ActivityResultContracts
import com.olequacircuits.carcardscanner.database.Car
import com.olequacircuits.carcardscanner.database.Waybill
import java.io.BufferedReader
import java.io.InputStreamReader


class ControlFragment : Fragment() {

    private lateinit var spTrain: Spinner
    private lateinit var tvStatus: TextView
    private lateinit var tvScanCount: TextView

    private lateinit var btnStartScanning: Button
    private lateinit var btnEndScanning: Button
    private lateinit var btnGenerateSwitchlist: Button

    private val viewModel: OperationsViewModel by activityViewModels()

//    private val trains = listOf(
//        "Tacoma Local",
//        "Everett Switcher",
//        "Seattle Turn"
//    )
    private var trains = listOf<Train>()

    private val locationCsvPicker =
        registerForActivityResult(
            ActivityResultContracts.OpenDocument()
        ) { uri: Uri? ->

            uri?.let {
                importLocationsFromCsv(it)
            }
        }

    private val carCsvPicker =
        registerForActivityResult(
            ActivityResultContracts.OpenDocument()
        ) { uri ->

            uri?.let {
                importCarsFromCsv(it)
            }
        }

    private val trainCsvPicker =
        registerForActivityResult(
            ActivityResultContracts.OpenDocument()
        ) { uri ->

            uri?.let {
                importTrainsFromCsv(it)
            }
        }

    private val waybillCsvPicker =
        registerForActivityResult(
            ActivityResultContracts.OpenDocument()
        ) { uri ->

            uri?.let {
                importWaybillsFromCsv(it)
            }
        }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(
            R.layout.fragment_control,
            container,
            false
        )
    }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?
    ) {
        super.onViewCreated(
            view,
            savedInstanceState
        )

        // TODO: remove this
        // DEVELOPMENT TEST BUTTONS
        // Remove/rework before release
        val btnTestDb = view.findViewById<Button>(R.id.btnTestDb)
        val btnImportAar = view.findViewById<Button>(R.id.btnImportAar)
        val btnShowAarCount = view.findViewById<Button>(R.id.btnShowAARCount)
        val btnImportLocations =
            view.findViewById<Button>(R.id.btnImportLocations)
        val btnLocationCount =
            view.findViewById<Button>(R.id.btnLocationCount)
        val btnImportCars =
            view.findViewById<Button>(R.id.btnImportCars)
        val btnCarCount =
            view.findViewById<Button>(R.id.btnCarCount)
        val btnImportTrains =
            view.findViewById<Button>(R.id.btnImportTrains)
        val btnImportWaybills =
            view.findViewById<Button>(R.id.btnImportWaybills)

        lifecycleScope.launch {

            withContext(Dispatchers.IO) {
                importAarCodesIfNeeded()
            }
        }

        btnImportWaybills.setOnClickListener {

            waybillCsvPicker.launch(
                arrayOf("text/*", "text/csv")
            )
        }

        btnImportTrains.setOnClickListener {

            trainCsvPicker.launch(
                arrayOf("text/*", "text/csv")
            )
        }

        btnCarCount.setOnClickListener {

            lifecycleScope.launch {

                val count = withContext(Dispatchers.IO) {

                    DatabaseProvider
                        .getDatabase(requireContext())
                        .carDao()
                        .getCount()
                }

                Toast.makeText(
                    requireContext(),
                    "Cars = $count",
                    Toast.LENGTH_LONG
                ).show()
            }
        }

        btnImportCars.setOnClickListener {

            carCsvPicker.launch(
                arrayOf("text/*", "text/csv")
            )
        }

        btnLocationCount.setOnClickListener {

            lifecycleScope.launch {

                val count = withContext(Dispatchers.IO) {

                    val db = DatabaseProvider.getDatabase(requireContext())

                    db.locationDao().getCount()
                }

                Toast.makeText(
                    requireContext(),
                    "Locations = $count",
                    Toast.LENGTH_LONG
                ).show()
            }
        }

        btnImportLocations.setOnClickListener {

            locationCsvPicker.launch(
                arrayOf("text/*", "text/csv")
            )
        }

        btnTestDb.setOnClickListener {

            lifecycleScope.launch {

                val db = DatabaseProvider.getDatabase(requireContext())

                val trains = withContext(Dispatchers.IO) {
                    db.trainDao().getAll()
                }

                val message = buildString {
                    append("Train count = ${trains.size}\n\n")

                    trains.forEach {
                        append("${it.trainId}  ${it.name}\n")
                    }
                }

                AlertDialog.Builder(requireContext())
                    .setTitle("Train Table")
                    .setMessage(message)
                    .setPositiveButton("OK", null)
                    .show()
            }
        }

        btnImportAar.setOnClickListener {

            lifecycleScope.launch {

                withContext(Dispatchers.IO) {
                    importAarCodesIfNeeded()
                }

                Toast.makeText(
                    requireContext(),
                    "AAR codes loaded",
                    Toast.LENGTH_LONG
                ).show()
            }
        }

        btnShowAarCount.setOnClickListener {

            lifecycleScope.launch {

                val count = withContext(Dispatchers.IO) {

                    val db = DatabaseProvider.getDatabase(requireContext())

                    db.aarCodeDao().getCount()
                }

                Toast.makeText(
                    requireContext(),
                    "AAR count = $count",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
        // TODO: end of temporary removal block

        spTrain = view.findViewById(R.id.spTrain)

        tvStatus =
            view.findViewById(R.id.tvStatus)

        tvScanCount =
            view.findViewById(R.id.tvScanCount)

        btnStartScanning =
            view.findViewById(R.id.btnStartScanning)

        btnEndScanning =
            view.findViewById(R.id.btnEndScanning)

        btnGenerateSwitchlist =
            view.findViewById(R.id.btnGenerateSwitchlist)


        lifecycleScope.launch {

            val db =
                DatabaseProvider.getDatabase(
                    requireContext()
                )

            val trains =
                withContext(Dispatchers.IO) {
                    db.trainDao().getAll()
                }
            Log.d("TRAIN", "Trains loaded: ${trains.size} $trains")
            val adapter = ArrayAdapter(
                requireContext(),
                android.R.layout.simple_spinner_item,
                trains
            )

            adapter.setDropDownViewResource(
                android.R.layout.simple_spinner_dropdown_item
            )

            spTrain.adapter = adapter

            spTrain.onItemSelectedListener =
                object : AdapterView.OnItemSelectedListener {

                    override fun onItemSelected(
                        parent: AdapterView<*>,
                        view: View?,
                        position: Int,
                        id: Long
                    ) {

                        val train =
                            parent.getItemAtPosition(position) as Train

                        viewModel.activeTrainId = train.trainId
                        viewModel.activeTrainName = train.name

                        Log.d(
                            "TRAIN",
                            "Selected train ${train.trainId} ${train.name}"
                        )
                    }

                    override fun onNothingSelected(
                        parent: AdapterView<*>
                    ) {
                        viewModel.activeTrainId = null
                        viewModel.activeTrainName = null
                    }
                }
        }




        btnStartScanning.setOnClickListener {
            if (viewModel.activeTrainId == null) {

                Toast.makeText(
                    requireContext(),
                    "Select a train first",
                    Toast.LENGTH_SHORT
                ).show()

                return@setOnClickListener
            }

            viewModel.activeTrainName =
                spTrain.selectedItem.toString()

            viewModel.scanSessionActive = true

            tvStatus.text = "Status: Scanning"
        }

        btnEndScanning.setOnClickListener {

            viewModel.scanSessionActive = false

            tvStatus.text = "Status: Not Scanning"
        }

        btnGenerateSwitchlist.setOnClickListener {

            // placeholder for later
            tvStatus.text =
                "Status: Switchlist generation not implemented"
        }
    }

    override fun onResume() {
        super.onResume()

        tvScanCount.text =
            "Cars Scanned: ${viewModel.scannedCars.size}"

        if (viewModel.scanSessionActive) {

            tvStatus.text = "Status: Scanning"

        } else {

            tvStatus.text = "Status: Not Scanning"
        }
    }

    private suspend fun importAarCodesIfNeeded() {

        val db =
            DatabaseProvider.getDatabase(requireContext())

        val existing =
            db.aarCodeDao().getCount()

        if (existing > 0) {
            return
        }

        val codes = mutableListOf<AARCode>()

        requireContext().assets
            .open("aar_codes.csv")
            .bufferedReader()
            .forEachLine { line ->

                if (line.isBlank()) return@forEachLine

                val fields = parseCsvLine(line)

                if (fields.size >= 4) {

                    codes.add(
                        AARCode(
                            aar = fields[0].trim(),
                            description = fields[3].trim()
                        )
                    )
                }
            }

        db.aarCodeDao()
            .insertAll(codes)
    }


    private fun importLocationsFromCsv(uri: Uri) {

        lifecycleScope.launch {

            val imported = withContext(Dispatchers.IO) {

                val db =
                    DatabaseProvider.getDatabase(requireContext())

                val locations = mutableListOf<Location>()

                val lines =
                    CsvImporter.readLines(
                        requireContext(),
                        uri
                    )

                // Skip header row
                for (line in lines.drop(1)) {

                    if (line.isBlank()) continue

                    val parts = line.split(",")

                    if (parts.size >= 2) {

                        locations.add(
                            Location(
                                locationId =
                                    parts[0].trim().toInt(),

                                name =
                                    parts[1].trim()
                            )
                        )
                    }
                }

                db.locationDao()
                    .insertAll(locations)

                locations.size
            }

            Toast.makeText(
                requireContext(),
                "Imported $imported locations",
                Toast.LENGTH_LONG
            ).show()
        }
    }


    private fun importCarsFromCsv(uri: Uri) {

        lifecycleScope.launch {
            var skipped = 0
            val result = withContext(Dispatchers.IO) {

                val db =
                    DatabaseProvider.getDatabase(requireContext())

                val lines =
                    CsvImporter.readLines(
                        requireContext(),
                        uri
                    )

                val cars = mutableListOf<Car>()

                // Skip header row
                for (line in lines.drop(1)) {

                    if (line.isBlank()) continue

                    val parts = line.split(",")

                    if (parts.size >= 5) {
                        val roadname = parts[0].trim()
                        val roadnum  = parts[1].trim()

                        if (roadname.isEmpty() || roadnum.isEmpty()) {
                            skipped++
                            Log.w("IMPORT", "Skipping invalid car row: $line")
                            continue
                        }

                        cars.add(
                            Car(
                                roadname = roadname,
                                roadnum = roadnum,

                                aarcode =
                                    parts[2].trim(),

                                length =
                                    parts[3].trim(),

                                color =
                                    parts[4].trim()
                            )
                        )
                    }
                }

                db.carDao()
                    .insertAll(cars)
                Pair(cars.size, skipped)
            }

            Toast.makeText(
                requireContext(),
                "Imported ${result.first} cars, skipped ${result.second}",
                Toast.LENGTH_LONG
            ).show()
        }
    }

    private fun importTrainsFromCsv(uri: Uri) {

        lifecycleScope.launch {

            val imported = withContext(Dispatchers.IO) {

                val db =
                    DatabaseProvider.getDatabase(requireContext())

                val lines =
                    CsvImporter.readLines(
                        requireContext(),
                        uri
                    )

                val trains = mutableListOf<Train>()

                // Skip header row
                for (line in lines.drop(1)) {

                    val name = line.trim()

                    if (name.isNotEmpty()) {

                        trains.add(
                            Train(
                                name = name
                            )
                        )
                    }
                }

                db.trainDao().insertAll(trains)

                trains.size
            }

            loadTrains()

            Toast.makeText(
                requireContext(),
                "Imported $imported trains",
                Toast.LENGTH_LONG
            ).show()
        }
    }

    private fun importWaybillsFromCsv(uri: Uri) {

        lifecycleScope.launch {

            val imported = withContext(Dispatchers.IO) {

                val db =
                    DatabaseProvider.getDatabase(
                        requireContext()
                    )

                val lines =
                    CsvImporter.readLines(
                        requireContext(),
                        uri
                    )

                val waybills =
                    mutableListOf<Waybill>()

                for (line in lines) {

                    val parts = line.split(",")

                    if (parts.size >= 3) {

                        waybills.add(

                            Waybill(

                                waybillId =
                                    parts[0].trim().toInt(),

                                carId =
                                    parts[1].trim(),

                                destinationId =
                                    parts[2].trim().toInt(),

                                loadStatus =
                                    if (parts.size >= 4)
                                        parts[3].trim()
                                    else
                                        null
                            )
                        )
                    }
                }

                db.waybillDao()
                    .insertAll(waybills)

                waybills.size
            }

            Toast.makeText(
                requireContext(),
                "Imported $imported waybills",
                Toast.LENGTH_LONG
            ).show()
        }
    }

    private fun loadTrains() {

        lifecycleScope.launch {

            val db =
                DatabaseProvider.getDatabase(requireContext())

            val trains =
                withContext(Dispatchers.IO) {
                    db.trainDao().getAll()
                }

            val adapter = ArrayAdapter(
                requireContext(),
                android.R.layout.simple_spinner_item,
                trains
            )

            adapter.setDropDownViewResource(
                android.R.layout.simple_spinner_dropdown_item
            )

            spTrain.adapter = adapter
        }
    }

    private fun parseCsvLine(line: String): List<String> {

        val result = mutableListOf<String>()
        val current = StringBuilder()

        var inQuotes = false

        for (c in line) {

            when {

                c == '"' -> {
                    inQuotes = !inQuotes
                }

                c == ',' && !inQuotes -> {
                    result.add(current.toString())
                    current.clear()
                }

                else -> {
                    current.append(c)
                }
            }
        }

        result.add(current.toString())

        return result
    }
}