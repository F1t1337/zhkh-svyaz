package com.example.communication.data.session

import android.content.Context

data class SessionData(
    val userId: String,
    val isAdmin: Boolean,
    val apartment: String,
    val entrance: String,
    val name: String = ""
)

object SessionManager {

    private const val PREFS = "zhkh_session"
    private const val KEY_USER_ID = "user_id"
    private const val KEY_IS_ADMIN = "is_admin"
    private const val KEY_APARTMENT = "apartment"
    private const val KEY_ENTRANCE = "entrance"
    private const val KEY_NAME = "name"

    fun save(context: Context, isAdmin: Boolean, userId: String, apartment: String, entrance: String, name: String = "") {
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
            .edit()
            .putString(KEY_USER_ID, userId)
            .putBoolean(KEY_IS_ADMIN, isAdmin)
            .putString(KEY_APARTMENT, apartment)
            .putString(KEY_ENTRANCE, entrance)
            .putString(KEY_NAME, name)
            .apply()
    }

    fun get(context: Context): SessionData? {
        val prefs = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
        val userId = prefs.getString(KEY_USER_ID, null) ?: return null
        return SessionData(
            userId = userId,
            isAdmin = prefs.getBoolean(KEY_IS_ADMIN, false),
            apartment = prefs.getString(KEY_APARTMENT, "") ?: "",
            entrance = prefs.getString(KEY_ENTRANCE, "") ?: "",
            name = prefs.getString(KEY_NAME, "") ?: ""
        )
    }

    fun clear(context: Context) {
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE).edit().clear().apply()
    }
}
