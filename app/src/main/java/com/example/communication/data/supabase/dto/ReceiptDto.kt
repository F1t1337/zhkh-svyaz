package com.example.communication.data.supabase.dto

import com.example.communication.data.models.Receipt
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ReceiptDto(
    val id: String,
    @SerialName("resident_id") val residentId: String,
    val period: String,
    @SerialName("cold_water") val coldWater: Double,
    @SerialName("hot_water") val hotWater: Double,
    val electricity: Double,
    val gas: Double,
    val garbage: Double,
    val maintenance: Double,
    @SerialName("total_amount") val totalAmount: Double,
    @SerialName("is_read") val isRead: Boolean,
    @SerialName("sent_at") val sentAt: String
)

fun ReceiptDto.toDomain() = Receipt(
    id = id,
    residentId = residentId,
    period = period,
    coldWater = coldWater,
    hotWater = hotWater,
    electricity = electricity,
    gas = gas,
    garbage = garbage,
    maintenance = maintenance,
    totalAmount = totalAmount,
    isRead = isRead,
    sentAt = sentAt
)
