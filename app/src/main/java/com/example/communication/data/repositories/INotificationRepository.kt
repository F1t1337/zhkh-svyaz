package com.example.communication.data.repositories

import com.example.communication.data.models.Notification

interface INotificationRepository {
    suspend fun getAll(aptId: String): List<Notification>
    suspend fun send(n: Notification): Boolean
    suspend fun markRead(id: String): Boolean
}