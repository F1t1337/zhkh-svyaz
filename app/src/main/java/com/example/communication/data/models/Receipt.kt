package com.example.communication.data.models

data class Receipt (
    val id: String,
    val residentId: String,
    val period: String,
    val coldWater: Double,
    val hotWater: Double,
    val electricity: Double,
    val gas: Double,
    val garbage: Double,
    val maintenance: Double,
    val totalAmount: Double,
    val isRead: Boolean,
    val sentAt: String
)