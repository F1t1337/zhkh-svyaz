package com.example.communication.data.repositories

import com.example.communication.data.models.Service
import com.example.communication.data.models.ServiceStatus

interface IServiceRepository {
    suspend fun getAll(): List<Service>
    suspend fun save(service: Service): Boolean
    suspend fun updateStatus(id: String, status: ServiceStatus): Boolean
}
