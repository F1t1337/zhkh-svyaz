package com.example.communication.data.repositories.supabase

import com.example.communication.data.models.Notification
import com.example.communication.data.repositories.INotificationRepository
import com.example.communication.data.supabase.SupabaseClientProvider
import com.example.communication.data.supabase.dto.NotificationDto
import com.example.communication.data.supabase.dto.toDomain
import com.example.communication.data.supabase.dto.toDto
import io.github.jan.supabase.postgrest.postgrest

class SupabaseNotificationRepository : INotificationRepository {

    private val db get() = SupabaseClientProvider.client.postgrest

    /**
     * Возвращает уведомления для квартиры:
     * - общие (target_apartments = '{}')
     * - адресованные конкретной квартире
     */
    override suspend fun getAll(aptId: String): List<Notification> =
        db.from("notifications")
            .select()
            .decodeList<NotificationDto>()
            .filter { it.targetApartments.isEmpty() || it.targetApartments.contains(aptId) }
            .map { it.toDomain() }

    override suspend fun send(n: Notification): Boolean {
        db.from("notifications").insert(n.toDto())
        return true
    }

    override suspend fun markRead(id: String): Boolean {
        db.from("notifications").update({
            set("is_read", true)
        }) {
            filter { eq("id", id) }
        }
        return true
    }
}
