package com.example.communication.data.models

sealed class User(
    open val id: String,
    open val password: String?
){
    data class regularUser(
        override val id: String,
        val phone: String,
        val passport: String,
        override val password: String?,
        val apartmentNumber: String,
        val entrance : String
    ) : User(id, password)

    data class adminUser(
        override val id: String,
        val admLogin: String,
        override val password: String?,
        val permissions: List<AdminPermission>,
    ) : User(id, password)
}

enum class AdminPermission {
    MANAGE_USERS,
    VIEW_ANALYTICS,
    EDIT_SETTINGS,
    DELETE_CONTENT,
    MANAGE_ADMINS
}
