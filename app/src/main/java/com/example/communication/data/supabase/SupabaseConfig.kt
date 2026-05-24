package com.example.communication.data.supabase

/**
 * Настройте SUPABASE_URL и SUPABASE_ANON_KEY перед запуском.
 *
 * SUPABASE_URL  — http://<IP-сервера>:8000
 * SUPABASE_ANON_KEY — anon-ключ из дашборда Supabase
 *   (Settings → API → Project API keys → anon / public)
 */
object SupabaseConfig {
    const val SUPABASE_URL = "http://YOUR_SERVER_IP:8000"
    const val SUPABASE_ANON_KEY = "YOUR_ANON_KEY_HERE"
}
