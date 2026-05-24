package com.example.communication.data.repositories

import com.example.communication.data.models.Request
import com.example.communication.data.models.RequestStatus

interface IRequestRepository {
    suspend fun getAll(residentId: String): List<Request>
    suspend fun getAll(): List<Request>
    suspend fun getById(id: String): Request
    suspend fun save(r: Request): Boolean
    suspend fun updateStatus(id: String, status: RequestStatus): Boolean
    suspend fun updateAdminResponse(id: String, response: String): Boolean
    suspend fun countAll(): Int
}