package com.example.communication

import android.app.Application
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.example.communication.presentation.utils.LocalNotificationHelper
import com.example.communication.service.NotificationSyncWorker
import java.util.concurrent.TimeUnit

class CommunicationApp : Application() {

    override fun onCreate() {
        super.onCreate()
        // Register the notification channel once (required on API 26+)
        LocalNotificationHelper.createChannel(this)
        // Schedule background sync
        scheduleNotificationSync()
    }

    private fun scheduleNotificationSync() {
        val request = PeriodicWorkRequestBuilder<NotificationSyncWorker>(
            15, TimeUnit.MINUTES
        ).build()

        WorkManager.getInstance(this).enqueueUniquePeriodicWork(
            NotificationSyncWorker.WORK_NAME,
            ExistingPeriodicWorkPolicy.KEEP,   // don't reschedule if already queued
            request
        )
    }
}
