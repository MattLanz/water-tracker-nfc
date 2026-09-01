package com.example.watertracker.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.icons.Icons
import androidx.compose.material3.icons.filled.Add
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.watertracker.viewmodel.HomeViewModel

@Composable
fun HomeScreen(viewModel: HomeViewModel = hiltViewModel()) {
    val totalToday = viewModel.todaysIntake.collectAsState(initial = 0f)
    Scaffold(
        topBar = { TopAppBar(title = { Text("Water Tracker") }) },
        floatingActionButton = {
            FloatingActionButton(onClick = { viewModel.startNfcScan() }) {
                Icon(Icons.Default.Add, contentDescription = "Tap Bottle")
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            Text(text = "${totalToday.value} L today", style = androidx.compose.material3.MaterialTheme.typography.headlineMedium)
        }
    }
}
