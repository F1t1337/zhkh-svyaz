package com.example.communication.domain.decorator

import android.util.Log
import com.example.communication.data.models.Notification
import com.example.communication.data.repositories.INotificationRepository

interface NotificationSender {
    suspend fun send(notification: Notification): Boolean
}

class RepositoryNotificationSender(
    private val repository: INotificationRepository
) : NotificationSender {
    override suspend fun send(notification: Notification): Boolean =
        repository.send(notification)
}

class LoggingNotificationDecorator(
    private val wrapped: NotificationSender
) : NotificationSender {
    override suspend fun send(notification: Notification): Boolean {
        Log.d("NotifSender", "Отправка: «${notification.title}» → ${notification.targetApartments.ifEmpty { listOf("все") }}")
        val result = wrapped.send(notification)
        Log.d("NotifSender", "Результат: $result")
        return result
    }
}

class RetryNotificationDecorator(
    private val wrapped: NotificationSender,
    private val maxRetries: Int = 3
) : NotificationSender {
    override suspend fun send(notification: Notification): Boolean {
        repeat(maxRetries) { attempt ->
            if (wrapped.send(notification)) return true
            Log.w("NotifSender", "Попытка ${attempt + 1} из $maxRetries не удалась")
        }
        return false
    }
}

fun buildNotificationSender(repository: INotificationRepository): NotificationSender =
    RetryNotificationDecorator(
        LoggingNotificationDecorator(
            RepositoryNotificationSender(repository)
        )
    )
