package com.example.communication.presentation.regular

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.communication.data.repositories.MockReceiptRepository
import com.example.communication.data.repositories.MockRequestRepository

class ResidentViewModelFactory : ViewModelProvider.Factory{
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ResidentViewModel::class.java)){
            return ResidentViewModel(
                requestRepository = MockRequestRepository(),
                receiptRepository = MockReceiptRepository()
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
    }
}