package com.example.watertracker.ui.stats

import android.widget.FrameLayout
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.watertracker.viewmodel.StatsViewModel
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.charts.LineChart

@Composable
fun StatsScreen(viewModel: StatsViewModel = hiltViewModel()) {
    val daily = viewModel.dailyEntries.collectAsState(initial = emptyList())
    val weekly = viewModel.weeklyEntries.collectAsState(initial = emptyList())
    val context = LocalContext.current
    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        // Daily line chart
        AndroidView(factory = { ctx ->
            LineChart(ctx).apply { layoutParams = FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, 300) }
        }, update = { chart ->
            ChartHelper.configureLineChart(chart, daily.value)
        })
        // Weekly bar chart
        AndroidView(factory = { ctx ->
            BarChart(ctx).apply { layoutParams = FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, 300) }
        }, update = { chart ->
            ChartHelper.configureBarChart(chart, weekly.value)
        })
    }
}
