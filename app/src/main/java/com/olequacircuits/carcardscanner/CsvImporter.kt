package com.olequacircuits.carcardscanner

import android.content.Context
import android.net.Uri
import java.io.BufferedReader
import java.io.InputStreamReader

object CsvImporter {

    fun readLines(
        context: Context,
        uri: Uri
    ): List<String> {

        val lines = mutableListOf<String>()

        context.contentResolver
            .openInputStream(uri)
            ?.use { input ->

                BufferedReader(
                    InputStreamReader(input)
                ).forEachLine { line ->

                    if (line.isNotBlank()) {
                        lines.add(line)
                    }
                }
            }

        return lines
    }
}