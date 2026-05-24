package com.example.communication.data.repositories.supabase

import com.example.communication.data.models.Service
import com.example.communication.data.models.ServiceStatus
import com.example.communication.data.repositories.IServiceRepository
import com.example.communication.data.supabase.SupabaseClientProvider
import com.example.communication.data.supabase.dto.ServiceDto
import com.example.communication.data.supabase.dto.toDomain
import com.example.communication.data.supabase.dto.toDto
import io.github.jan.supabase.postgrest.postgrest

class SupabaseServiceRepository : IServiceRepository {

    private val db get() = SupabaseClientProvider.client.postgrest

    override suspend fun getAll(): List<Service> =
        db.from("services")
            .select()
            .decodeList<ServiceDto>()
            .map { it.toDomain() }

    override suspend fun save(service: Service): Boolean {
        db.from("services").insert(service.toDto())
        return true
    }

    override suspend fun updateStatus(id: String, status: ServiceStatus): Boolean {
        db.from("services").update({
            set("status", status.name)
        }) {
            filter { eq("id", id) }
        }
        return true
    }
}
