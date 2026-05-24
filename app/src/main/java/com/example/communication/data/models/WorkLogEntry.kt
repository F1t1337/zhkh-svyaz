package com.example.communication.data.models

data class WorkLogEntry (
    val id: String,
    val workType: String,
    val location: String,
    val description: String,
    val performedAt: String,
    val reportPdfUrl: String,
    val adminId: String
)