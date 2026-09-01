package com.example.watertracker.infra.reminder

import android.content.Context
import androidx.hilt.work.HiltWorkerFactory
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import java.util.concurrent.TimeUnit
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class ReminderScheduler @Inject constructor(@ApplicationContext private val context: Context) {
    fun schedule() {
        val workRequest = PeriodicWorkRequestBuilder<WaterReminderWorker>(2, TimeUnit.HOURS)
            .build()
        WorkManager.getInstance(context)
            .enqueueUniquePeriodicWork(
                "water_reminder",
                ExistingPeriodicWorkPolicy.KEEP,
                workRequest
            )
    }
}
