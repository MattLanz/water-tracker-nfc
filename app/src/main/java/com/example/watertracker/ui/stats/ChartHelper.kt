package com.example.watertracker.ui.stats

import android.content.Context
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet

object ChartHelper {
    fun configureLineChart(chart: LineChart, entries: List<Entry>) {
        val dataSet = LineDataSet(entries, "Daily Intake").apply {
            setDrawValues(false)
        }
        chart.data = LineData(dataSet)
        chart.description.isEnabled = false
        chart.invalidate()
    }

    fun configureBarChart(chart: BarChart, entries: List<BarEntry>) {
        val dataSet = BarDataSet(entries, "Weekly Intake").apply {
            setDrawValues(false)
        }
        chart.data = BarData(dataSet)
        chart.description.isEnabled = false
        chart.invalidate()
    }
}
