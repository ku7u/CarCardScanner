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

class ControlFragment : Fragment() {

    private lateinit var spTrain: Spinner
    private lateinit var tvStatus: TextView
    private lateinit var tvScanCount: TextView

    private lateinit var btnStartScanning: Button
    private lateinit var btnEndScanning: Button
    private lateinit var btnGenerateSwitchlist: Button

    private val viewModel: OperationsViewModel by activityViewModels()

    private val trains = listOf(
        "Tacoma Local",
        "Everett Switcher",
        "Seattle Turn"
    )

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

        val btnTestDb = view.findViewById<Button>(R.id.btnTestDb)
        val btnImportAar = view.findViewById<Button>(R.id.btnImportAar)
        val btnShowAarCount = view.findViewById<Button>(R.id.btnShowAARCount)

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

                val imported = withContext(Dispatchers.IO) {

                    val db = DatabaseProvider.getDatabase(requireContext())

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

                    db.aarCodeDao().insertAll(codes)

                    codes.size
                }

                Toast.makeText(
                    requireContext(),
                    "Imported $imported AAR codes",
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

        val adapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_item,
            trains
        )

        adapter.setDropDownViewResource(
            android.R.layout.simple_spinner_dropdown_item
        )

        spTrain.adapter = adapter

        btnStartScanning.setOnClickListener {

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