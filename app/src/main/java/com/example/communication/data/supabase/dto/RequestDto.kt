package com.example.communication.data.supabase.dto

import com.example.communication.data.models.Request
import com.example.communication.data.models.RequestCategory
import com.example.communication.data.models.RequestStatus
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class RequestDto(
    val id: String,
    @SerialName("resident_id") val residentId: String,
    val category: String,
    val description: String,
    val attachments: List<String> = emptyList(),
    val status: String,
    @SerialName("created_at") val createdAt: String,
    val deadline: String
)

fun RequestDto.toDomain() = Request(
    id = id,
    residentId = residentId,
    category = runCatching { RequestCategory.valueOf(category) }.getOrDefault(RequestCategory.OTHER),
    description = description,
    attachments = attachments,
    status = runCatching { RequestStatus.valueOf(status) }.getOrDefault(RequestStatus.NEW),
    createdAt = createdAt,
    deadline = deadline
)

fun Request.toDto() = RequestDto(
    id = id,
    residentId = residentId,
    category = category.name,
    description = description,
    attachments = attachments,
    status = status.name,
    createdAt = createdAt,
    deadline = deadline
)
