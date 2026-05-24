package com.example.communication.domain.usecases.auth

import com.example.communication.data.models.User
import com.example.communication.data.repositories.AuthRepository

class Login (private val authRepository : AuthRepository){
    suspend operator fun invoke(identifier: String, password: String): Result<User>{
        if (identifier.isBlank()){
            return Result.failure(Exception("Введите логин или номер телефона"))
        }
        if (password.isBlank()){
            return Result.failure(Exception("Введите пароль или номер паспорта"))
        }
        if (password.length < 8){
            return Result.failure(Exception("Пароль должен содержать не менее 8 символов"))
        }

        return when{
            identifier.matches(Regex("^\\d{10,11}$")) ->{
                authRepository.login(identifier, password)
            }
            else -> {
                authRepository.loginAdmin(identifier, password)
            }
        }


    }


}



