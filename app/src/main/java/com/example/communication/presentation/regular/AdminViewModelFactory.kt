package com.example.communication.presentation.regular

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.communication.data.repositories.supabase.SupabaseNotificationRepository
import com.example.communication.data.repositories.supabase.SupabaseRequestRepository
import com.example.communication.data.repositories.supabase.SupabaseServiceRepository
import com.example.communication.data.repositories.supabase.SupabaseWorkLogRepository

class AdminViewModelFactory : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AdminViewModel::class.java)) {
            return AdminViewModel(
                requestRepository = SupabaseRequestRepository(),
                notificationRepository = SupabaseNotificationRepository(),
                workLogRepository = SupabaseWorkLogRepository(),
                serviceRepository = SupabaseServiceRepository()
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
    }
}
