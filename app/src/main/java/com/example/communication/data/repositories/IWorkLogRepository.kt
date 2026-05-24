package com.example.communication.data.repositories

import com.example.communication.data.models.WorkLogEntry

interface IWorkLogRepository {
    suspend fun getAll(): List<WorkLogEntry>
    suspend fun save(entry: WorkLogEntry): Boolean
}
