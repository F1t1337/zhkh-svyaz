package com.example.communication.data.repositories.supabase

import com.example.communication.data.models.WorkLogEntry
import com.example.communication.data.repositories.IWorkLogRepository
import com.example.communication.data.supabase.SupabaseClientProvider
import com.example.communication.data.supabase.dto.WorkLogEntryDto
import com.example.communication.data.supabase.dto.toDomain
import com.example.communication.data.supabase.dto.toDto
import io.github.jan.supabase.postgrest.postgrest

class SupabaseWorkLogRepository : IWorkLogRepository {

    private val db get() = SupabaseClientProvider.client.postgrest

    override suspend fun getAll(): List<WorkLogEntry> =
        db.from("work_log_entries")
            .select()
            .decodeList<WorkLogEntryDto>()
            .map { it.toDomain() }

    override suspend fun save(entry: WorkLogEntry): Boolean {
        db.from("work_log_entries").insert(entry.toDto())
        return true
    }
}
