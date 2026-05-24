package com.example.communication.presentation.utils

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import com.example.communication.R
import com.example.communication.presentation.regular.CoreActivity

object LocalNotificationHelper {

    const val CHANNEL_ID = "zhkh_notifications"
    private const val CHANNEL_NAME = "ЖКХ Связь — Уведомления"
    private const val PREFS = "notif_sync_prefs"
    private const val KEY_LAST_SEEN = "last_seen_millis"

    /** Call once at app startup to register the notification channel (API 26+). */
    fun createChannel(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                CHANNEL_NAME,
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Уведомления от управляющей компании"
                enableLights(true)
                enableVibration(true)
            }
            val mgr = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            mgr.createNotificationChannel(channel)
        }
    }

    /** Show a system notification that opens CoreActivity on tap. */
    fun show(context: Context, notifId: Int, title: String, body: String) {
        val intent = Intent(context, CoreActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent = PendingIntent.getActivity(
            context, notifId, intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notif = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle(title)
            .setContentText(body)
            .setStyle(NotificationCompat.BigTextStyle().bigText(body))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()

        val mgr = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        mgr.notify(notifId, notif)
    }

    /** Returns the timestamp of the last notification that was shown as a system push. */
    fun getLastSeenMillis(context: Context): Long =
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
            .getLong(KEY_LAST_SEEN, 0L)

    /** Saves the timestamp so we don't show the same notification twice. */
    fun saveLastSeenMillis(context: Context, millis: Long) {
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
            .edit().putLong(KEY_LAST_SEEN, millis).apply()
    }
}
