package com.olequacircuits.carcardscanner

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.Spinner
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels

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
}