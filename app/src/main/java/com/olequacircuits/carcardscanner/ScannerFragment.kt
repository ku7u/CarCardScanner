package com.olequacircuits.carcardscanner

import android.Manifest
import android.content.pm.PackageManager
import androidx.core.content.ContextCompat
import android.content.Context
import android.media.AudioManager
import android.media.ToneGenerator
import android.os.Build
import android.os.Bundle
import android.os.VibrationEffect
import android.os.Vibrator
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.TextView
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.fragment.app.Fragment
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.common.InputImage
import androidx.camera.core.ExperimentalGetImage
import androidx.fragment.app.activityViewModels


class ScannerFragment : Fragment() {

    private lateinit var previewView: PreviewView
    private lateinit var tvCount: TextView
    private lateinit var tvLastScan: TextView
    private lateinit var tvScannedCars: TextView
    private lateinit var spLocation: Spinner
    private val scannedCars = mutableSetOf<String>()
    private val viewModel: OperationsViewModel by activityViewModels()
    private lateinit var toneGenerator: ToneGenerator
    private lateinit var vibrator: Vibrator
    private lateinit var tvTrain: TextView

    private var scanCount = 0
    private val locations = listOf(
        "Tacoma Yard (101)",
        "Everett Paper Mill (205)",
        "Seattle Fuel (330)"
    )

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(
            R.layout.fragment_scanner,
            container,
            false
        )
    }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?
    ) {
        super.onViewCreated(view, savedInstanceState)

        previewView = view.findViewById(R.id.previewView)
        tvCount = view.findViewById(R.id.tvCount)
        tvLastScan = view.findViewById(R.id.tvLastScan)
        tvScannedCars =
            view.findViewById(R.id.tvScannedCars)
        spLocation = view.findViewById(R.id.spLocation)

        // initialize spinner
        // initialize camera
        // initialize scanner
        val spinner = view.findViewById<Spinner>(R.id.spLocation)

        val adapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_item,
            locations

        )

        adapter.setDropDownViewResource(
            android.R.layout.simple_spinner_dropdown_item
        )

        spinner.adapter = adapter
//        startCamera()
        if (ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            startCamera()
        } else {
            requestPermissions(
                arrayOf(Manifest.permission.CAMERA),
                100
            )
        }

        tvTrain = view.findViewById(R.id.tvTrain)

        tvTrain.text =
            "Train: ${viewModel.activeTrainName ?: "None"}"

        toneGenerator =
            ToneGenerator(
                AudioManager.STREAM_NOTIFICATION,
                100
            )

        vibrator =
            requireContext()
                .getSystemService(Context.VIBRATOR_SERVICE)
                    as Vibrator
    }

    @OptIn(ExperimentalGetImage::class)
    private fun startCamera() {
        val cameraProviderFuture =
            ProcessCameraProvider.getInstance(requireContext())

        cameraProviderFuture.addListener({

            val cameraProvider =
                cameraProviderFuture.get()


            val preview =
                Preview.Builder()
                    .build()

            preview.setSurfaceProvider(
                previewView.surfaceProvider
            )


            val scanner =
                BarcodeScanning.getClient()


            val analysis =
                ImageAnalysis.Builder()
                    .setBackpressureStrategy(
                        ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST
                    )
                    .build()


            analysis.setAnalyzer(
                ContextCompat.getMainExecutor(requireContext())
            ) { imageProxy ->


                val mediaImage =
                    imageProxy.image


                if (mediaImage != null) {

                    val image =
                        InputImage.fromMediaImage(
                            mediaImage,
                            imageProxy.imageInfo.rotationDegrees
                        )


                    scanner.process(image)
                        .addOnSuccessListener @androidx.annotation.RequiresPermission(android.Manifest.permission.VIBRATE) { barcodes ->

                            for (barcode in barcodes) {

                                val value =
                                    barcode.rawValue

                                if (value != null &&
                                    viewModel.scannedCarSet.add(value)
                                ) {
                                    val qrParts = value.split(",")
                                    val carId = qrParts[0]
                                    val destinationId =
                                        if (qrParts.size > 1)
                                            qrParts[1]
                                        else
                                            ""

                                    val scanCount = viewModel.scannedCars.size + 1
                                    viewModel.scannedCars.add(value)

                                    toneGenerator.startTone(
                                        ToneGenerator.TONE_PROP_BEEP,
                                        100
                                    )

                                    if (Build.VERSION.SDK_INT >= 26) {

                                        vibrator.vibrate(
                                            VibrationEffect.createOneShot(
                                                80,
                                                VibrationEffect.DEFAULT_AMPLITUDE
                                            )
                                        )

                                    } else {

                                        @Suppress("DEPRECATION")
                                        vibrator.vibrate(80)
                                    }


                                    requireActivity().runOnUiThread {

                                        tvCount.text = "Scanned Cars: $scanCount"
                                        tvLastScan.text =
                                            "Car: $carId  Dest: $destinationId"
                                        tvScannedCars.text =
                                            viewModel.scannedCars.joinToString("\n")
                                    }
                                }

                            }

                        }
                        .addOnCompleteListener {

                            imageProxy.close()

                        }

                } else {

                    imageProxy.close()
                }
            }

            try {
                cameraProvider.unbindAll()

                cameraProvider.bindToLifecycle(
                    viewLifecycleOwner,
                    CameraSelector.DEFAULT_BACK_CAMERA,
                    preview,
                    analysis
                )

                Log.d("CAMERA", "bindToLifecycle succeeded")

            } catch (e: Exception) {

                Log.e("CAMERA", "bindToLifecycle FAILED", e)
            }

        }, ContextCompat.getMainExecutor(requireContext()))
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(
            requestCode,
            permissions,
            grantResults
        )

        if (requestCode == 100 &&
            grantResults.isNotEmpty() &&
            grantResults[0] == PackageManager.PERMISSION_GRANTED
        ) {
            startCamera()
        }
    }
}