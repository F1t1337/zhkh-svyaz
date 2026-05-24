package com.example.communication.data.supabase.dto

import com.example.communication.data.models.Service
import com.example.communication.data.models.ServiceStatus
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ServiceDto(
    val id: String,
    @SerialName("service_type") val serviceType: String,
    val description: String = "",
    @SerialName("scheduled_at") val scheduledAt: String,
    @SerialName("resident_id") val residentId: String,
    @SerialName("apartment_number") val apartmentNumber: String = "",
    val status: String
)

fun ServiceDto.toDomain() = Service(
    id = id,
    serviceType = serviceType,
    description = description,
    scheduledAt = scheduledAt,
    residentId = residentId,
    apartmentNumber = apartmentNumber,
    status = runCatching { ServiceStatus.valueOf(status) }.getOrDefault(ServiceStatus.SCHEDULED)
)

fun Service.toDto() = ServiceDto(
    id = id,
    serviceType = serviceType,
    description = description,
    scheduledAt = scheduledAt,
    residentId = residentId,
    apartmentNumber = apartmentNumber,
    status = status.name
)
