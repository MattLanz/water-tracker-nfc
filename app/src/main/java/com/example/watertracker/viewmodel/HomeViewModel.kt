package com.example.watertracker.viewmodel

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.watertracker.domain.repository.WaterIntakeRepository
import com.example.watertracker.domain.repository.TagRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val intakeRepo: WaterIntakeRepository,
    private val tagRepo: TagRepository,
    private val context: Context
) : ViewModel() {

    private val _todaysIntake = MutableStateFlow(0f)
    val todaysIntake: StateFlow<Float> = _todaysIntake

    private val _pendingUid = MutableStateFlow<String?>(null)
    val pendingUid: StateFlow<String?> = _pendingUid

    private val unknownTagReceiver = object : BroadcastReceiver() {
        override fun onReceive(ctx: Context?, intent: Intent?) {
            intent?.getStringExtra(NfcReaderService.EXTRA_UID)?.let { uid ->
                _pendingUid.value = uid
            }
        }
    }

    init {
        val filter = IntentFilter(NfcReaderService.ACTION_UNKNOWN_TAG)
        context.registerReceiver(unknownTagReceiver, filter)
        // Load today's total (simplified – sum all entries for today)
        viewModelScope.launch {
            // TODO: implement proper date filtering
            val all = intakeRepo.getAllIntakes()
            _todaysIntake.value = all.sumOf { it.liters }
        }
    }

    fun startNfcScan() {
        // The Activity should enable foreground dispatch and forward the tag to NfcReaderService.handleTag(tag)
        // This method is a placeholder to trigger UI flow.
    }

    fun registerNewTag(uid: String, capacityLiters: Float) {
        viewModelScope.launch {
            tagRepo.registerTag(uid, capacityLiters)
            _pendingUid.value = null
            // Update today's intake if needed
            val all = intakeRepo.getAllIntakes()
            _todaysIntake.value = all.sumOf { it.liters }
        }
    }

    override fun onCleared() {
        super.onCleared()
        context.unregisterReceiver(unknownTagReceiver)
    }
}
