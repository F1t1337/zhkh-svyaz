package com.example.communication.data.supabase.dto

import kotlinx.serialization.Serializable

@Serializable
data class SettingDto(
    val key: String,
    val value: String
)
