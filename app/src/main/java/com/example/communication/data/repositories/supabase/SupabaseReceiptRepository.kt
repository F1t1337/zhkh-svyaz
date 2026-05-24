package com.example.communication.data.repositories.supabase

import com.example.communication.data.models.Receipt
import com.example.communication.data.repositories.IReceiptRepository
import com.example.communication.data.supabase.SupabaseClientProvider
import com.example.communication.data.supabase.dto.ReceiptDto
import com.example.communication.data.supabase.dto.toDomain
import io.github.jan.supabase.postgrest.postgrest

class SupabaseReceiptRepository : IReceiptRepository {

    private val db get() = SupabaseClientProvider.client.postgrest

    override suspend fun getByResident(residentId: String): List<Receipt> =
        db.from("receipts")
            .select { filter { eq("resident_id", residentId) } }
            .decodeList<ReceiptDto>()
            .map { it.toDomain() }

    override suspend fun getById(id: String): Receipt =
        db.from("receipts")
            .select { filter { eq("id", id) } }
            .decodeSingle<ReceiptDto>()
            .toDomain()

    override suspend fun markRead(id: String): Boolean {
        db.from("receipts").update({
            set("is_read", true)
        }) {
            filter { eq("id", id) }
        }
        return true
    }

    override suspend fun generateMonthly() {
        // В боевом приложении — генерация квитанций на сервере (Cloud Function / Edge Function)
    }
}
