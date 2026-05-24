package com.example.communication.presentation.auth

import com.example.communication.data.models.User

sealed class AuthUiState {
    object Idle    : AuthUiState()
    object Loading : AuthUiState()
    data class Success(val user: User)      : AuthUiState()
    data class Error(val message: String)   : AuthUiState()
}
