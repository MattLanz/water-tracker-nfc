package com.example.watertracker.infra.nfc

import android.app.Service
import android.content.Intent
import android.nfc.NfcAdapter
import android.nfc.Tag
import android.os.IBinder
import com.example.watertracker.domain.repository.TagRepository
import com.example.watertracker.domain.repository.WaterIntakeRepository
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class NfcReaderService : Service() {
    companion object {
        const val ACTION_UNKNOWN_TAG = "com.example.watertracker.ACTION_UNKNOWN_TAG"
        const val EXTRA_UID = "extra_uid"
    }

    @Inject lateinit var tagRepo: TagRepository
    @Inject lateinit var intakeRepo: WaterIntakeRepository
    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        return START_STICKY
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
        serviceScope.cancel()
    }
}
