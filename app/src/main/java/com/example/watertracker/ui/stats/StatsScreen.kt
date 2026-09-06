package com.example.watertracker.ui.stats

import android.widget.FrameLayout
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.watertracker.viewmodel.StatsViewModel
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.data.Entry

@Composable
fun StatsScreen(viewModel: StatsViewModel = hiltViewModel()) {
    val daily by viewModel.dailyEntries.collectAsState()
    val weekly by viewModel.weeklyEntries.collectAsState()
    
    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        // Daily line chart
        AndroidView(
            factory = { ctx ->
                LineChart(ctx).apply { 
                    layoutParams = FrameLayout.LayoutParams(
                        FrameLayout.LayoutParams.MATCH_PARENT, 
                        600 // Increased height for visibility
                    ) 
                }
            }, 
            modifier = Modifier.padding(bottom = 16.dp),
            update = { chart ->
                val entries = daily.mapIndexed { index, pair -> 
                    Entry(index.toFloat(), pair.second) 
                }
                ChartHelper.configureLineChart(chart, entries)
            }
        )
        // Weekly bar chart
        AndroidView(
            factory = { ctx ->
                BarChart(ctx).apply { 
                    layoutParams = FrameLayout.LayoutParams(
                        FrameLayout.LayoutParams.MATCH_PARENT, 
                        600 
                    ) 
                }
            }, 
            update = { chart ->
                val entries = weekly.mapIndexed { index, pair -> 
                    BarEntry(index.toFloat(), pair.second) 
                }
                ChartHelper.configureBarChart(chart, entries)
            }
        )
    }
}
