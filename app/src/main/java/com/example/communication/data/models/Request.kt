package com.example.communication.data.models

enum class RequestCategory {
    PLUMBING,
    ELECTRICITY,
    CLEANING,
    REPAIR,
    OTHER
}

enum class RequestStatus {
    NEW,
    IN_PROGRESS,
    DONE,
    REJECTED
}

data class Request(
    val id: String,
    val residentId: String,
    val category: RequestCategory,
    val description: String,
    val attachments: List<String>,
    val status: RequestStatus,
    val createdAt: String,
    val deadline: String,
    val adminResponse: String? = null
)