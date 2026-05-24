package com.example.communication.presentation.regular

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.communication.data.models.Notification
import com.example.communication.data.models.Request
import com.example.communication.data.models.Service
import com.example.communication.data.models.WorkLogEntry
import com.example.communication.data.repositories.INotificationRepository
import com.example.communication.data.repositories.IRequestRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class AdminViewModel(
    private val requestRepository: IRequestRepository,
    private val notificationRepository: INotificationRepository
    ) : ViewModel() {
    private val _requests = MutableStateFlow<List<Request>>(emptyList())
    val requests: StateFlow<List<Request>> = _requests.asStateFlow()

    private val _workLog = MutableStateFlow<List<WorkLogEntry>>(emptyList())
    val workLog: StateFlow<List<WorkLogEntry>> = _workLog.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    fun loadAllRequests(){
        viewModelScope.launch {
            _isLoading.value = true
            _requests.value = requestRepository.getAll()
            _isLoading.value = false
        }
    }

    fun sendNotification(n: Notification) {
        viewModelScope.launch {
            notificationRepository.send(n)
        }
    }

    fun assignService(s: Service) {
    }

    fun addWorkLogEntry(e: WorkLogEntry) {
    }
}