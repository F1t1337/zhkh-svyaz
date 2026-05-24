package com.example.communication.presentation.regular

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.communication.domain.usecases.auth.Logout
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
class CoreViewModel(private val logout: Logout) : ViewModel() {

    private val _uiState = MutableStateFlow<CoreUiState>(CoreUiState.Active)
    val uiState: StateFlow<CoreUiState> = _uiState.asStateFlow()

    fun logout() {
        viewModelScope.launch {
            logout() // вызов Logout.invoke()
            _uiState.value = CoreUiState.LoggedOut
        }
    }
}
