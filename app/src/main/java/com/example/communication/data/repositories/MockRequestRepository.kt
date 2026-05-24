package com.example.communication.data.repositories

import com.example.communication.data.mock.MockData
import com.example.communication.data.models.Request
import com.example.communication.data.models.RequestStatus

class MockRequestRepository : IRequestRepository {
    private val _requests = MockData.requests.toMutableList()

    override suspend fun getAll(residentId: String) = _requests.filter { it.residentId == residentId }
    override suspend fun getAll() = _requests.toList()
    override suspend fun getById(id: String) = _requests.first { it.id == id }

    override suspend fun save(r: Request): Boolean {
        _requests.add(r)
        return true
    }

    override suspend fun updateStatus(id: String, status: RequestStatus): Boolean {
        val idx = _requests.indexOfFirst { it.id == id }
        if (idx < 0) return false
        _requests[idx] = _requests[idx].copy(status = status)
        return true
    }
}
