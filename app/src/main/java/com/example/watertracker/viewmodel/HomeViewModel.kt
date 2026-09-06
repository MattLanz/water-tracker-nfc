package com.example.watertracker.viewmodel

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.nfc.Tag
import android.util.Log
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.watertracker.domain.repository.WaterIntakeRepository
import com.example.watertracker.domain.repository.TagRepository
import com.example.watertracker.infra.nfc.NfcReaderService
import com.example.watertracker.infra.nfc.NfcUtils
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val intakeRepo: WaterIntakeRepository,
    private val tagRepo: TagRepository,
    @ApplicationContext private val context: Context
) : ViewModel() {

    private val _todaysIntake = MutableStateFlow(0f)
    val todaysIntake: StateFlow<Float> = _todaysIntake

    private val _pendingUid = MutableStateFlow<String?>(null)
    val pendingUid: StateFlow<String?> = _pendingUid

    private val _isScanning = MutableStateFlow(false)
    val isScanning: StateFlow<Boolean> = _isScanning

    private val unknownTagReceiver = object : BroadcastReceiver() {
        override fun onReceive(ctx: Context?, intent: Intent?) {
            Log.d("HomeViewModel", "Received unknown tag broadcast")
            intent?.getStringExtra(NfcReaderService.EXTRA_UID)?.let { uid ->
                _pendingUid.value = uid
                _isScanning.value = false
            }
        }
    }

    init {
        Log.d("HomeViewModel", "Initializing HomeViewModel")
        val filter = IntentFilter(NfcReaderService.ACTION_UNKNOWN_TAG)
        try {
            // Explicitly use ContextCompat to handle API-specific registration flags
            ContextCompat.registerReceiver(
                context,
                unknownTagReceiver,
                filter,
                ContextCompat.RECEIVER_NOT_EXPORTED
            )
        } catch (e: Exception) {
            Log.e("HomeViewModel", "Failed to register receiver", e)
        }
        refreshIntake()
    }

    private fun refreshIntake() {
        viewModelScope.launch {
            val all = intakeRepo.getAllIntakes()
            val total = all.sumOf { it.liters.toDouble() }.toFloat()
            Log.d("HomeViewModel", "Refreshed intake: $total L")
            _todaysIntake.value = total
        }
    }

    fun startNfcScan() {
        Log.d("HomeViewModel", "Starting NFC Scan")
        _isScanning.value = true
    }

    fun stopNfcScan() {
        Log.d("HomeViewModel", "Stopping NFC Scan")
        _isScanning.value = false
    }

    fun processTag(tag: Tag) {
        val uid = NfcUtils.uidFromTag(tag)
        Log.d("HomeViewModel", "Processing tag UID: $uid")
        viewModelScope.launch {
            val tagInfo = tagRepo.getTagInfo(uid)
            if (tagInfo != null) {
                Log.d("HomeViewModel", "Tag recognized: ${tagInfo.capacityLiters} L")
                intakeRepo.addIntake(System.currentTimeMillis(), tagInfo.capacityLiters)
                _isScanning.value = false
                refreshIntake()
            } else {
                Log.d("HomeViewModel", "Tag unknown, prompting registration")
                _pendingUid.value = uid
                _isScanning.value = false
            }
        }
    }

    fun registerNewTag(uid: String, capacityLiters: Float) {
        Log.d("HomeViewModel", "Registering new tag $uid with $capacityLiters L")
        viewModelScope.launch {
            tagRepo.registerTag(uid, capacityLiters)
            _pendingUid.value = null
            if (capacityLiters > 0) {
                intakeRepo.addIntake(System.currentTimeMillis(), capacityLiters)
            }
            refreshIntake()
        }
    }

    fun clearPendingTag() {
        _pendingUid.value = null
    }

    override fun onCleared() {
        super.onCleared()
        try {
            context.unregisterReceiver(unknownTagReceiver)
        } catch (e: Exception) {
            Log.e("HomeViewModel", "Failed to unregister receiver", e)
        }
    }
}
