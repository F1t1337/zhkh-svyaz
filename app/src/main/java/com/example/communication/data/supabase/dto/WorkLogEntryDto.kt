package com.example.communication.data.supabase.dto

import com.example.communication.data.models.WorkLogEntry
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class WorkLogEntryDto(
    val id: String,
    @SerialName("work_type") val workType: String,
    val location: String,
    val description: String,
    @SerialName("performed_at") val performedAt: String,
    @SerialName("report_pdf_url") val reportPdfUrl: String,
    @SerialName("admin_id") val adminId: String
)

fun WorkLogEntryDto.toDomain() = WorkLogEntry(
    id = id,
    workType = workType,
    location = location,
    description = description,
    performedAt = performedAt,
    reportPdfUrl = reportPdfUrl,
    adminId = adminId
)

fun WorkLogEntry.toDto() = WorkLogEntryDto(
    id = id,
    workType = workType,
    location = location,
    description = description,
    performedAt = performedAt,
    reportPdfUrl = reportPdfUrl,
    adminId = adminId
)
