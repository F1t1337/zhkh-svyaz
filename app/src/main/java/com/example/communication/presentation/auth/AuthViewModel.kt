package com.example.communication.presentation.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.communication.domain.usecases.auth.Login
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class AuthViewModel(private val loginUseCase: Login) : ViewModel() {

    private val _uiState = MutableStateFlow<AuthUiState>(AuthUiState.Idle)
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()

    fun login(identifier: String, password: String) {
        if (_uiState.value is AuthUiState.Loading) return

        viewModelScope.launch {
            _uiState.value = AuthUiState.Loading

            val result = loginUseCase(identifier, password)

            _uiState.value = if (result.isSuccess) {
                AuthUiState.Success(result.getOrNull()!!)
            } else {
                AuthUiState.Error(result.exceptionOrNull()?.message ?: "Неизвестная ошибка")
            }
        }
    }
}
