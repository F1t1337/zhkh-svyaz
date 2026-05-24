package com.example.communication.presentation.regular

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.communication.domain.usecases.auth.Logout


class CoreViewModelFactory(
    private val logout: Logout
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(CoreViewModel::class.java)) {
            return CoreViewModel(logout) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
    }
}
