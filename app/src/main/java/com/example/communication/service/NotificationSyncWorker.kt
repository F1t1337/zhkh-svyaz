package com.example.communication.service

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.communication.data.repositories.supabase.SupabaseNotificationRepository
import com.example.communication.data.session.SessionManager
import com.example.communication.presentation.utils.LocalNotificationHelper
import java.text.SimpleDateFormat
import java.util.Locale

/**
 * Background worker that polls Supabase for new notifications and shows
 * Android system notifications (status bar) for any that arrived after the
 * last known timestamp. Runs every 15 minutes via WorkManager.
 */
class NotificationSyncWorker(
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {

    private val sdf = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())

    override suspend fun doWork(): Result {
        return try {
            // Only residents receive push notifications
            val session = SessionManager.get(applicationContext) ?: return Result.success()
            if (session.isAdmin) return Result.success()
            val apartment = session.apartment.ifBlank { return Result.success() }

            val lastSeen = LocalNotificationHelper.getLastSeenMillis(applicationContext)

            val notifications = SupabaseNotificationRepository().getAll(apartment)

            var newLastSeen = lastSeen
            notifications
                .sortedBy { it.sentAt }
                .forEach { notif ->
                    val millis = parseMillis(notif.sentAt)
                    if (millis > lastSeen) {
                        LocalNotificationHelper.show(
                            applicationContext,
                            notif.id.hashCode(),
                            notif.title,
                            notif.body
                        )
                        if (millis > newLastSeen) newLastSeen = millis
                    }
                }

            if (newLastSeen > lastSeen) {
                LocalNotificationHelper.saveLastSeenMillis(applicationContext, newLastSeen)
            }

            Result.success()
        } catch (e: Exception) {
            // Retry on network/DB failure; don't crash
            Result.retry()
        }
    }

    private fun parseMillis(isoString: String): Long =
        runCatching { sdf.parse(isoString.take(19))?.time ?: 0L }.getOrDefault(0L)

    companion object {
        const val WORK_NAME = "notification_sync"
    }
}
