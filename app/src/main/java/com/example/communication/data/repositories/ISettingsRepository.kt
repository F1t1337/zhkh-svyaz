package com.example.communication.data.repositories

interface ISettingsRepository {
    suspend fun get(key: String): String?
    suspend fun set(key: String, value: String)
}
