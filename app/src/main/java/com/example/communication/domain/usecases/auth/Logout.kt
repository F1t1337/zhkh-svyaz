package com.example.communication.domain.usecases.auth

import com.example.communication.data.repositories.AuthRepository

class Logout(private val authRepository: AuthRepository){
    suspend operator fun invoke(){
        authRepository.logout()
    }
}