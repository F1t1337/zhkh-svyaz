package com.example.communication.data.models

enum class NotificationType {
    GENERAL,
    REQUEST_UPDATE,
    RECEIPT,
    EMERGENCY
}

data class Notification(
    val id: String,
    val title: String,
    val body: String,
    val type: NotificationType,
    val targetApartments: List<String>,
    val sentAt: String,
    val isRead: Boolean
)