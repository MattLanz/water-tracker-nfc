package com.example.watertracker

import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import com.example.watertracker.ui.HomeScreen
import com.example.watertracker.viewmodel.HomeViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val viewModel: HomeViewModel by viewModels()
    private var nfcAdapter: NfcAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        // Start NFC service if user enabled background logging (placeholder for Settings flag)
        val prefs = getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
        val enableBackground = prefs.getBoolean("enable_background_logging", false)
        if (enableBackground) {
            val serviceIntent = Intent(this, com.example.watertracker.infra.service.WaterTrackerService::class.java)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                startForegroundService(serviceIntent)
            } else {
                startService(serviceIntent)
            }
        }
        super.onCreate(savedInstanceState)
        nfcAdapter = NfcAdapter.getDefaultAdapter(this)

        setContent {
            val isScanning by viewModel.isScanning.collectAsState()

            LaunchedEffect(isScanning) {
                if (isScanning) {
                    nfcAdapter?.enableReaderMode(
                        this@MainActivity,
                        { tag -> viewModel.processTag(tag) },
                        NfcAdapter.FLAG_READER_NFC_A or NfcAdapter.FLAG_READER_NFC_B or NfcAdapter.FLAG_READER_SKIP_NDEF_CHECK,
                        null
                    )
                } else {
                    nfcAdapter?.disableReaderMode(this@MainActivity)
                }
            }

            MaterialTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    HomeScreen(viewModel = viewModel)
                }
            }
        }
    }

    override fun onPause() {
        super.onPause()
        nfcAdapter?.disableReaderMode(this)
        viewModel.stopNfcScan()
    }
}
