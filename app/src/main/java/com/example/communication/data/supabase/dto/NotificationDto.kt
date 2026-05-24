package com.example.communication.data.supabase.dto

import com.example.communication.data.models.Notification
import com.example.communication.data.models.NotificationType
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class NotificationDto(
    val id: String,
    val title: String,
    val body: String,
    val type: String,
    @SerialName("target_apartments") val targetApartments: List<String> = emptyList(),
    @SerialName("sent_at") val sentAt: String,
    @SerialName("is_read") val isRead: Boolean
)

fun NotificationDto.toDomain() = Notification(
    id = id,
    title = title,
    body = body,
    type = runCatching { NotificationType.valueOf(type) }.getOrDefault(NotificationType.GENERAL),
    targetApartments = targetApartments,
    sentAt = sentAt,
    isRead = isRead
)

fun Notification.toDto() = NotificationDto(
    id = id,
    title = title,
    body = body,
    type = type.name,
    targetApartments = targetApartments,
    sentAt = sentAt,
    isRead = isRead
)
