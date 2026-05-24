package com.example.communication.data.models


enum class ServiceStatus {
    SCHEDULED,
    IN_PROGRESS,
    COMPLETED,
    CANCELLED
}

data class Service(
    val id: String,
    val title: String,
    val scheduledAt: String,
    val residentId: String,
    val status: ServiceStatus
)