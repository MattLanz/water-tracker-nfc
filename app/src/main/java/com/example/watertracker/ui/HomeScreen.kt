package com.example.watertracker.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.watertracker.ui.RegistrationBottomSheet

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(viewModel: HomeViewModel = hiltViewModel()) {
    val totalToday by viewModel.todaysIntake.collectAsState()
    val isScanning by viewModel.isScanning.collectAsState()
    val pendingUid by viewModel.pendingUid.collectAsState()

    Scaffold(
        topBar = { TopAppBar(title = { Text("Water Tracker") }) }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
                .background(
                    brush = androidx.compose.ui.graphics.Brush.verticalGradient(
                        colors = listOf(
                            androidx.compose.ui.graphics.Color(0xFFB2EBF2), // teal light
                            androidx.compose.ui.graphics.Color(0xFFE0F7FA)  // lighter
                        )
                    )
                ),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "%.2f L today".format(totalToday),
                style = MaterialTheme.typography.headlineLarge
            )

            // Scanning banner / progress
            if (isScanning) {
                Spacer(modifier = Modifier.height(32.dp))
                // Pulsing animation placeholder – using CircularProgressIndicator for now
                CircularProgressIndicator()
                Text(
                    text = "Scanning for NFC tag...",
                    modifier = Modifier.padding(top = 8.dp)
                )
                Spacer(modifier = Modifier.height(16.dp))
                Button(onClick = { viewModel.stopNfcScan() }) {
                    Text("Cancel")
                }
            } else {
                // Show tap banner when not scanning
                Text(
                    text = "Tap bottle to log a drink",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }
        }

        pendingUid?.let { uid ->
            RegistrationBottomSheet(
                uid = uid,
                onDismiss = { viewModel.clearPendingTag() },
                onRegister = { capacity -> viewModel.registerNewTag(uid, capacity) }
            )
        }
    }
}
