package com.example.communication.data.repositories

import com.example.communication.data.mock.MockData
import com.example.communication.data.models.Notification

class MockNotificationRepository : INotificationRepository{
    override suspend fun getAll(aptId: String): List<Notification> {
        return MockData.notifications.filter { it.targetApartments.contains(aptId) }
    }

    override suspend fun markRead(id: String): Boolean = true

    override suspend fun send(n: Notification): Boolean = true
}