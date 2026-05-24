package com.example.communication.data.repositories.supabase

import com.example.communication.data.models.User
import com.example.communication.data.repositories.AuthRepository
import com.example.communication.data.supabase.SupabaseClientProvider
import com.example.communication.data.supabase.dto.AdminDto
import com.example.communication.data.supabase.dto.ResidentDto
import com.example.communication.data.supabase.dto.toDomain
import io.github.jan.supabase.postgrest.postgrest
import io.github.jan.supabase.postgrest.query.filter.PostgrestFilterBuilder
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class SupabaseAuthRepository : AuthRepository {

    private val db get() = SupabaseClientProvider.client.postgrest

    private val _currentUser = MutableStateFlow<User?>(null)
    override val currentUser: StateFlow<User?> = _currentUser.asStateFlow()

    override suspend fun login(phoneNumber: String, password: String): Result<User> {
        return runCatching {
            val residents = db.from("residents")
                .select {
                    filter { eq("phone", phoneNumber) }
                }
                .decodeList<ResidentDto>()

            val resident = residents.firstOrNull()
                ?: return Result.failure(Exception("Пользователь не найден"))

            val isValid = if (resident.password != null) {
                password == resident.password
            } else {
                password == resident.passport
            }

            if (!isValid) return Result.failure(Exception("Неверный пароль или данные паспорта"))

            val user = resident.toDomain()
            _currentUser.value = user
            user
        }.recoverCatching { e ->
            throw Exception("Ошибка подключения: ${e.message}")
        }
    }

    override suspend fun loginAdmin(adminLogin: String, password: String): Result<User> {
        return runCatching {
            val admins = db.from("admins")
                .select {
                    filter { eq("adm_login", adminLogin) }
                }
                .decodeList<AdminDto>()

            val admin = admins.firstOrNull()
                ?: return Result.failure(Exception("Администратор не найден"))

            if (password != admin.password) {
                return Result.failure(Exception("Неверный пароль"))
            }

            val user = admin.toDomain()
            _currentUser.value = user
            user
        }.recoverCatching { e ->
            throw Exception("Ошибка подключения: ${e.message}")
        }
    }

    override suspend fun logout() {
        _currentUser.value = null
    }

    override suspend fun isAuthenticated(): Boolean = _currentUser.value != null

    override suspend fun changePassword(identifier: String, passport: String, newPassword: String): Result<Unit> {
        return runCatching {
            val residents = db.from("residents")
                .select { filter { eq("phone", identifier) } }
                .decodeList<ResidentDto>()
            val resident = residents.firstOrNull()
                ?: return Result.failure(Exception("Пользователь не найден"))
            if (resident.passport != passport) {
                return Result.failure(Exception("Паспортные данные не совпадают"))
            }
            db.from("residents").update({
                set("password", newPassword)
            }) {
                filter { eq("id", resident.id) }
            }
        }
    }

    override suspend fun countResidents(): Int =
        runCatching {
            db.from("residents").select().decodeList<ResidentDto>().size
        }.getOrDefault(0)

    override suspend fun getResidentApartments(): List<Pair<String, String>> =
        runCatching {
            db.from("residents").select().decodeList<ResidentDto>()
                .map { it.apartmentNumber to it.id }
        }.getOrDefault(emptyList())
}
