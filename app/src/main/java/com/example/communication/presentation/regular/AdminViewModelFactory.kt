package com.example.communication.presentation.regular

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.communication.data.repositories.MockNotificationRepository
import com.example.communication.data.repositories.MockRequestRepository
import com.example.communication.data.repositories.MockServiceRepository
import com.example.communication.data.repositories.MockWorkLogRepository

class AdminViewModelFactory : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AdminViewModel::class.java)) {
            return AdminViewModel(
                requestRepository = MockRequestRepository(),
                notificationRepository = MockNotificationRepository(),
                workLogRepository = MockWorkLogRepository(),
                serviceRepository = MockServiceRepository()
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
    }
}
