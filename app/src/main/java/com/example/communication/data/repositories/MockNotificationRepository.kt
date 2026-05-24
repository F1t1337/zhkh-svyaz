package com.example.communication.data.repositories

import com.example.communication.data.mock.MockData
import com.example.communication.data.models.Notification

class MockNotificationRepository : INotificationRepository {
    private val _notifications = MockData.notifications.toMutableList()

    override suspend fun getAll(aptId: String): List<Notification> =
        _notifications.filter { it.targetApartments.isEmpty() || it.targetApartments.contains(aptId) }

    override suspend fun markRead(id: String): Boolean = true

    override suspend fun send(n: Notification): Boolean {
        _notifications.add(n)
        return true
    }
}
