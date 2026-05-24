package com.example.communication.data.repositories

import com.example.communication.data.mock.MockData
import com.example.communication.data.models.Request
import com.example.communication.data.models.RequestStatus

class MockRequestRepository : IRequestRepository {
    override suspend fun getAll(residentId: String): List<Request> {
        return MockData.requests.filter { it.residentId == residentId }
    }

    override suspend fun getById(id: String): Request {
        return MockData.requests.first{it.id == id}
    }

    override suspend fun getAll(): List<Request> {
        return MockData.requests
    }

    override suspend fun save(r: Request): Boolean = true
    override suspend fun updateStatus(id: String, status: RequestStatus): Boolean = true
}