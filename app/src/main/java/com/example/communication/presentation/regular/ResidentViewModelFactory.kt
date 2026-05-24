package com.example.communication.presentation.regular

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.communication.data.repositories.supabase.SupabaseNotificationRepository
import com.example.communication.data.repositories.supabase.SupabaseReceiptRepository
import com.example.communication.data.repositories.supabase.SupabaseRequestRepository
import com.example.communication.data.repositories.supabase.SupabaseWorkLogRepository

class ResidentViewModelFactory : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ResidentViewModel::class.java)) {
            return ResidentViewModel(
                requestRepository = SupabaseRequestRepository(),
                receiptRepository = SupabaseReceiptRepository(),
                notificationRepository = SupabaseNotificationRepository(),
                workLogRepository = SupabaseWorkLogRepository()
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
    }
}
