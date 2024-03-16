package com.example.notyscan

import android.app.Activity
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import coil.compose.AsyncImage
import com.example.notyscan.ui.theme.NotyScanTheme
import com.google.mlkit.vision.documentscanner.GmsDocumentScanner
import com.google.mlkit.vision.documentscanner.GmsDocumentScannerOptions
import com.google.mlkit.vision.documentscanner.GmsDocumentScannerOptions.RESULT_FORMAT_JPEG
import com.google.mlkit.vision.documentscanner.GmsDocumentScannerOptions.RESULT_FORMAT_PDF
import com.google.mlkit.vision.documentscanner.GmsDocumentScannerOptions.SCANNER_MODE_FULL
import com.google.mlkit.vision.documentscanner.GmsDocumentScanning
import com.google.mlkit.vision.documentscanner.GmsDocumentScanningResult

class textReco : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val options = GmsDocumentScannerOptions.Builder()
            .setScannerMode(SCANNER_MODE_FULL)
            .setResultFormats(RESULT_FORMAT_JPEG, RESULT_FORMAT_PDF)
            .setGalleryImportAllowed(true)
            .setPageLimit(5)
            .build()

        val scanner = GmsDocumentScanning.getClient(options)



        setContent {
            NotyScanTheme {
                var imageUris by remember{ mutableStateOf<List<Uri>>(emptyList()) }
                val scannerLauncher = rememberLauncherForActivityResult(
                    contract = ActivityResultContracts.StartIntentSenderForResult()
                ) {
                    if (it.resultCode == RESULT_OK) {
                        val result = GmsDocumentScanningResult.fromActivityResultIntent(it.data)
                        if (result != null) {
                            imageUris = result.pages?.map { it.imageUri } ?: emptyList()
                        }

                    }
                }

                Column(
                    Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {

                    imageUris.forEach {
                        AsyncImage(
                            model = it, contentDescription = null,
                            contentScale = ContentScale.FillWidth,
                            modifier = Modifier.fillMaxWidth()
                            )
                    }
                    Button(onClick = {
                        scanner.getStartScanIntent(this@textReco)
                            .addOnSuccessListener { intentSender ->
                                scannerLauncher.launch(IntentSenderRequest.Builder(intentSender).build())

                            }
                            .addOnFailureListener {
                                Toast.makeText(this@textReco,"cannt scan",Toast.LENGTH_SHORT).show()
                                Log.e("scanErr", it.message.toString() )
                            }
                    }) {
                        Text(text = "Scan")
                    }



                }


            }
        }
    }
}

@Preview
@Composable
fun ScannerPreview () {
    val options = GmsDocumentScannerOptions.Builder()
        .setScannerMode(SCANNER_MODE_FULL)
        .setResultFormats(RESULT_FORMAT_JPEG, RESULT_FORMAT_PDF)
        .setGalleryImportAllowed(true)
        .setPageLimit(5)
        .build()
    val context = LocalContext.current
    val scanner = GmsDocumentScanning.getClient(options)
    var imageUris by remember{ mutableStateOf<List<Uri>>(emptyList()) }
    val scannerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartIntentSenderForResult()
    ) {
        if (it.resultCode == ComponentActivity.RESULT_OK) {
            val result = GmsDocumentScanningResult.fromActivityResultIntent(it.data)
            if (result != null) {
                imageUris = result.pages?.map { it.imageUri } ?: emptyList()
            }

        }
    }

    Column(
        Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {

        imageUris.forEach {
            AsyncImage(
                model = it, contentDescription = null,
                contentScale = ContentScale.FillWidth,
                modifier = Modifier.fillMaxWidth()
            )
            Button(onClick = {
                scanner.getStartScanIntent(context as Activity)
                    .addOnSuccessListener { intentSender ->
                        scannerLauncher.launch(IntentSenderRequest.Builder(intentSender).build())

                    }
                    .addOnFailureListener {
                        Toast.makeText(context,"cannt scan",Toast.LENGTH_SHORT).show()
                        Log.e("scanErr", it.message.toString() )
                    }
            }) {
                Text(text = "Scan")
            }

        }



    }
}g
