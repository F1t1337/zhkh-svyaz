package com.example.communication.data.repositories

import com.example.communication.data.models.User
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import com.example.communication.data.mock.MockData

interface AuthRepository {
    val currentUser: StateFlow<User?>
    suspend fun login(phoneNumber: String, password: String): Result<User>
    suspend fun loginAdmin(adminLogin: String, password: String): Result<User>
    suspend fun logout()
    suspend fun isAuthenticated(): Boolean
}

class AuthRepositoryImpl : AuthRepository {
    private val _currentUser = MutableStateFlow<User?>(null)
    override val currentUser: StateFlow<User?> = _currentUser.asStateFlow()

    override suspend fun login(phoneNumber: String, password: String): Result<User> {
        return try {
            val user = MockData.regUsers.find { it.phone == phoneNumber }

            if (user == null) {
                return Result.failure(Exception("Пользователь не найден"))
            }

            val isValidPassword = when {
                user.password != null -> password == user.password
                else -> password == user.passport
            }

            if (!isValidPassword) {
                return Result.failure(Exception("Неверный пароль или паспортные данные"))
            }

            _currentUser.value = user
            Result.success(user)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun loginAdmin(adminLogin: String, password: String): Result<User> {
        return try {
            val user = MockData.admUsers.find {it.admLogin == adminLogin}

            if (user == null) {
                return Result.failure(Exception("Пользователь не найден"))
            }

            if (password != "da, sosal") {
                return Result.failure(Exception("Неверный пароль"))
            }

            _currentUser.value = user
            Result.success(user)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun logout() {
        _currentUser.value = null
    }

    override suspend fun isAuthenticated(): Boolean = _currentUser.value != null
}