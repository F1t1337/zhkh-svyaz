package com.example.communication.data.repositories.supabase

import com.example.communication.data.models.Request
import com.example.communication.data.models.RequestStatus
import com.example.communication.data.repositories.IRequestRepository
import com.example.communication.data.supabase.SupabaseClientProvider
import com.example.communication.data.supabase.dto.RequestDto
import com.example.communication.data.supabase.dto.toDomain
import com.example.communication.data.supabase.dto.toDto
import io.github.jan.supabase.postgrest.postgrest

class SupabaseRequestRepository : IRequestRepository {

    private val db get() = SupabaseClientProvider.client.postgrest

    override suspend fun getAll(residentId: String): List<Request> =
        db.from("requests")
            .select { filter { eq("resident_id", residentId) } }
            .decodeList<RequestDto>()
            .map { it.toDomain() }

    override suspend fun getAll(): List<Request> =
        db.from("requests")
            .select()
            .decodeList<RequestDto>()
            .map { it.toDomain() }

    override suspend fun getById(id: String): Request =
        db.from("requests")
            .select { filter { eq("id", id) } }
            .decodeSingle<RequestDto>()
            .toDomain()

    override suspend fun save(r: Request): Boolean {
        db.from("requests").insert(r.toDto())
        return true
    }

    override suspend fun updateStatus(id: String, status: RequestStatus): Boolean {
        db.from("requests").update({
            set("status", status.name)
        }) {
            filter { eq("id", id) }
        }
        return true
    }
}
