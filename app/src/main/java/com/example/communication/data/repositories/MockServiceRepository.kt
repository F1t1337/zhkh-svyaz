package com.example.communication.data.repositories

import com.example.communication.data.mock.MockData
import com.example.communication.data.models.Service
import com.example.communication.data.models.ServiceStatus

class MockServiceRepository : IServiceRepository {
    private val _services = MockData.services.toMutableList()

    override suspend fun getAll(): List<Service> = _services.toList()

    override suspend fun save(service: Service): Boolean {
        _services.add(service)
        return true
    }

    override suspend fun updateStatus(id: String, status: ServiceStatus): Boolean {
        val idx = _services.indexOfFirst { it.id == id }
        if (idx < 0) return false
        _services[idx] = _services[idx].copy(status = status)
        return true
    }
}
