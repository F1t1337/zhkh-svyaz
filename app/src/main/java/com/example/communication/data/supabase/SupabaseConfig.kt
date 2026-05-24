package com.example.communication.data.supabase

/**
 * Подключение к Supabase Self-hosted.
 *
 * SUPABASE_URL      — http://<ВАШ_СТАТИЧЕСКИЙ_IP>:8000
 *                     Например: http://85.143.22.17:8000
 *
 * SUPABASE_ANON_KEY — anon-ключ из дашборда Supabase
 *                     Studio (http://<IP>:3000) → Settings → API →
 *                     Project API keys → anon / public
 *
 * Порт-форвардинг на роутере: внешний 8000 → сервер:8000 (TCP)
 * Windows Firewall: разрешить входящий TCP 8000
 */
object SupabaseConfig {
    // TODO: вставьте ваш статический IP и anon-ключ
    const val SUPABASE_URL = "http://YOUR_STATIC_IP:8000"
    const val SUPABASE_ANON_KEY = "YOUR_ANON_KEY_HERE"
}
