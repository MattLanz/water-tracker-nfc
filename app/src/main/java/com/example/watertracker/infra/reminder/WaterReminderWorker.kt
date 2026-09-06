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
import androidx.hilt.work.HiltWorker
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.first

@HiltWorker
class WaterReminderWorker @AssistedInject constructor(
    @Assisted private val context: Context,
    @Assisted workerParams: WorkerParameters,
    private val intakeRepo: WaterIntakeRepository,
    private val prefsRepo: UserPrefsRepository
) : CoroutineWorker(context, workerParams) {

    companion object {
        const val CHANNEL_ID = "water_reminder"
        const val NOTIFICATION_ID = 1001
    }

    override suspend fun doWork(): Result {
        val todayIntake = intakeRepo.getAllIntakes().sumOf { it.liters.toDouble() }.toFloat()
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
