package com.example.watertracker.infra.reminder

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.watertracker.domain.repository.WaterIntakeRepository
import com.example.watertracker.data.local.UserPrefsRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.android.HiltAndroidApp
import dagger.hilt.android.HiltWorker
import kotlinx.coroutines.flow.first
import javax.inject.Inject

@HiltWorker
class WaterReminderWorker @Inject constructor(
    @ApplicationContext private val context: Context,
    private val intakeRepo: WaterIntakeRepository,
    private val prefsRepo: UserPrefsRepository,
    workerParams: WorkerParameters
) : CoroutineWorker(context, workerParams) {

    companion object {
        const val CHANNEL_ID = "water_reminder"
        const val NOTIFICATION_ID = 1001
    }

    override suspend fun doWork(): Result {
        val todayIntake = intakeRepo.getAllIntakes().sumOf { it.liters }
        val goal = prefsRepo.dailyGoal.first()
        if (todayIntake < goal) {
            sendNotification("Stay hydrated", "You have logged $todayIntake L today (goal: $goal L)")
        }
        return Result.success()
    }

    private fun sendNotification(title: String, text: String) {
        val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(CHANNEL_ID, "Water Reminder", NotificationManager.IMPORTANCE_DEFAULT)
            manager.createNotificationChannel(channel)
        }
        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle(title)
            .setContentText(text)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .build()
        manager.notify(NOTIFICATION_ID, notification)
    }
}
