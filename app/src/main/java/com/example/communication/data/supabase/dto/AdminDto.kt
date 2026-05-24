package com.example.communication.data.supabase.dto

import com.example.communication.data.models.AdminPermission
import com.example.communication.data.models.User
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class AdminDto(
    val id: String,
    @SerialName("adm_login") val admLogin: String,
    val password: String,
    val permissions: List<String> = emptyList()
)

fun AdminDto.toDomain() = User.adminUser(
    id = id,
    admLogin = admLogin,
    password = password,
    permissions = permissions.mapNotNull { name ->
        runCatching { AdminPermission.valueOf(name) }.getOrNull()
    }
)
