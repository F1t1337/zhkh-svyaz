package com.example.communication.data.repositories

import com.example.communication.data.mock.MockData
import com.example.communication.data.models.WorkLogEntry

class MockWorkLogRepository : IWorkLogRepository {
    private val _entries = MockData.workLogEntries.toMutableList()

    override suspend fun getAll(): List<WorkLogEntry> = _entries.toList()

    override suspend fun save(entry: WorkLogEntry): Boolean {
        _entries.add(entry)
        return true
    }
}
