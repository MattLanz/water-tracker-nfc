package com.example.watertracker.infra.nfc

import android.app.Service
import android.content.Intent
import android.nfc.NfcAdapter
import android.nfc.Tag
import android.os.IBinder
import com.example.watertracker.domain.repository.TagRepository
import com.example.watertracker.domain.repository.WaterIntakeRepository
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class NfcReaderService : Service() {
    companion object {
        const val ACTION_UNKNOWN_TAG = "com.example.watertracker.ACTION_UNKNOWN_TAG"
        const val EXTRA_UID = "extra_uid"
    }

    @Inject lateinit var tagRepo: TagRepository
    @Inject lateinit var intakeRepo: WaterIntakeRepository
    private val nfcAdapter: NfcAdapter? by lazy { NfcAdapter.getDefaultAdapter(this) }

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        // Service is started when the activity registers for foreground dispatch.
        return START_STICKY
    }

    // This method should be called from the Activity's onNewIntent when an NFC tag is discovered.
    fun handleTag(tag: Tag) {
        val uid = NfcUtils.uidFromTag(tag)
        // Use coroutine scope (e.g., lifecycleScope) in the Activity; here we launch a simple thread for demo.
        Thread {
            val tagInfo = tagRepo.getTagInfo(uid)
            if (tagInfo != null) {
                // Known bottle – add intake with its capacity.
                intakeRepo.addIntake(System.currentTimeMillis(), tagInfo.capacityLiters)
            } else {
                // Unknown – broadcast to UI to prompt registration.
                val broadcast = Intent(ACTION_UNKNOWN_TAG).apply { putExtra(EXTRA_UID, uid) }
                sendBroadcast(broadcast)
            }
        }.start()
    }
}
