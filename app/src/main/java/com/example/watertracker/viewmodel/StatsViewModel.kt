package com.example.watertracker.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.watertracker.domain.repository.WaterIntakeRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class StatsViewModel @Inject constructor(
    private val intakeRepo: WaterIntakeRepository
) : ViewModel() {

    private val _dailyEntries = MutableStateFlow<List<Pair<Long, Float>>>(emptyList())
    val dailyEntries: StateFlow<List<Pair<Long, Float>>> = _dailyEntries

    private val _weeklyEntries = MutableStateFlow<List<Pair<String, Float>>>(emptyList())
    val weeklyEntries: StateFlow<List<Pair<String, Float>>> = _weeklyEntries

    init {
        loadStats()
    }

    private fun loadStats() {
        viewModelScope.launch {
            val all = intakeRepo.getAllIntakes()
            // Simplified aggregation for now
            _dailyEntries.value = all.takeLast(7).map { it.timestamp to it.liters }
            _weeklyEntries.value = listOf("Week 1" to all.sumOf { it.liters.toDouble() }.toFloat())
        }
    }
}
