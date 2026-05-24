package com.example.communication.data.repositories.supabase

import com.example.communication.data.repositories.ISettingsRepository
import com.example.communication.data.supabase.SupabaseClientProvider
import com.example.communication.data.supabase.dto.SettingDto
import io.github.jan.supabase.postgrest.postgrest

class SupabaseSettingsRepository : ISettingsRepository {

    private val db get() = SupabaseClientProvider.client.postgrest

    override suspend fun get(key: String): String? =
        runCatching {
            db.from("settings")
                .select { filter { eq("key", key) } }
                .decodeList<SettingDto>()
                .firstOrNull()?.value
        }.getOrNull()

    override suspend fun set(key: String, value: String) {
        runCatching {
            // Try update first
            db.from("settings").update({
                set("value", value)
            }) {
                filter { eq("key", key) }
            }
        }
    }
}
