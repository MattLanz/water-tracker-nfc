package com.example.watertracker.infra.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.nfc.NfcAdapter
import android.nfc.Tag
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import com.example.watertracker.domain.repository.TagRepository
import com.example.watertracker.domain.repository.WaterIntakeRepository
import com.example.watertracker.infra.nfc.NfcUtils
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class WaterTrackerService : Service() {
    companion object {
        const val ACTION_UNKNOWN_TAG = "com.example.watertracker.ACTION_UNKNOWN_TAG"
        const val EXTRA_UID = "extra_uid"
        private const val NOTIF_CHANNEL_ID = "water_tracker_service"
        private const val NOTIF_ID = 1
    }

    @Inject lateinit var tagRepo: TagRepository
    @Inject lateinit var intakeRepo: WaterIntakeRepository
    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private var nfcAdapter: NfcAdapter? = null

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onCreate() {
        super.onCreate()
        startForeground(NOTIF_ID, createNotification())
        nfcAdapter = NfcAdapter.getDefaultAdapter(this)
        nfcAdapter?.enableReaderMode(
            this,
            { tag -> handleTag(tag) },
            NfcAdapter.FLAG_READER_NFC_A or NfcAdapter.FLAG_READER_NFC_B or NfcAdapter.FLAG_READER_SKIP_NDEF_CHECK,
            null
        )
    }

    private fun createNotification(): Notification {
        val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                NOTIF_CHANNEL_ID,
                "Water Tracker Service",
                NotificationManager.IMPORTANCE_LOW
            ).apply { description = "Runs NFC scanning in background" }
            manager.createNotificationChannel(channel)
        }
        return NotificationCompat.Builder(this, NOTIF_CHANNEL_ID)
            .setContentTitle("Water Tracker")
            .setContentText("Scanning for bottle…")
            .setSmallIcon(android.R.drawable.ic_menu_info_details)
            .setOngoing(true)
            .build()
    }

    fun handleTag(tag: Tag) {
        val uid = NfcUtils.uidFromTag(tag)
        serviceScope.launch {
            val tagInfo = tagRepo.getTagInfo(uid)
            if (tagInfo != null) {
                intakeRepo.addIntake(System.currentTimeMillis(), tagInfo.capacityLiters)
            } else {
                val broadcast = Intent(ACTION_UNKNOWN_TAG).apply {
                    putExtra(EXTRA_UID, uid)
                    setPackage(packageName)
                }
                sendBroadcast(broadcast)
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        nfcAdapter?.disableReaderMode(this)
        serviceScope.cancel()
    }
}
