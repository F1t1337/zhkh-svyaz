package com.example.communication.presentation.regular

sealed class CoreUiState {
    object Active    : CoreUiState()
    object LoggedOut : CoreUiState()
}
