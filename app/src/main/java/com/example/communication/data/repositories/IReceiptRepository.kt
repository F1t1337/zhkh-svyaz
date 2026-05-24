package com.example.communication.data.repositories

import com.example.communication.data.models.Receipt

interface IReceiptRepository {
    suspend fun getByResident(residentId: String): List<Receipt>
    suspend fun getById(id: String): Receipt
    suspend fun markRead(id: String): Boolean
    suspend fun generateMonthly()
}