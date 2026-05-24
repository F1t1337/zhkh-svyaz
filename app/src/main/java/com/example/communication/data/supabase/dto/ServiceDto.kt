package com.example.communication.data.supabase.dto

import com.example.communication.data.models.Service
import com.example.communication.data.models.ServiceStatus
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ServiceDto(
    val id: String,
    val title: String,
    @SerialName("scheduled_at") val scheduledAt: String,
    @SerialName("resident_id") val residentId: String,
    val status: String
)

fun ServiceDto.toDomain() = Service(
    id = id,
    title = title,
    scheduledAt = scheduledAt,
    residentId = residentId,
    status = runCatching { ServiceStatus.valueOf(status) }.getOrDefault(ServiceStatus.SCHEDULED)
)

fun Service.toDto() = ServiceDto(
    id = id,
    title = title,
    scheduledAt = scheduledAt,
    residentId = residentId,
    status = status.name
)
