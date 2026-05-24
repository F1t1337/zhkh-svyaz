package com.example.communication.data.repositories

import com.example.communication.data.mock.MockData
import com.example.communication.data.models.Receipt

class MockReceiptRepository : IReceiptRepository {
    override suspend fun getByResident(residentId: String): List<Receipt> {
        return MockData.receipts.filter{it.residentId == residentId}
    }

    override suspend fun getById(id: String): Receipt {
        return MockData.receipts.first{it.id == id}
    }

    override suspend fun markRead(id: String): Boolean = true

    override suspend fun generateMonthly() {
        TODO("Not yet implemented")
    }
}