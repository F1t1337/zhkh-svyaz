package com.example.communication.presentation.regular

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.communication.data.models.Notification
import com.example.communication.data.models.Receipt
import com.example.communication.data.models.Request
import com.example.communication.data.models.WorkLogEntry
import com.example.communication.data.repositories.INotificationRepository
import com.example.communication.data.repositories.IReceiptRepository
import com.example.communication.data.repositories.IRequestRepository
import com.example.communication.data.repositories.IWorkLogRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ResidentViewModel(
    private val requestRepository: IRequestRepository,
    private val receiptRepository: IReceiptRepository,
    private val notificationRepository: INotificationRepository,
    private val workLogRepository: IWorkLogRepository
) : ViewModel() {

    private val _requests = MutableStateFlow<List<Request>>(emptyList())
    val requests: StateFlow<List<Request>> = _requests.asStateFlow()

    private val _receipts = MutableStateFlow<List<Receipt>>(emptyList())
    val receipts: StateFlow<List<Receipt>> = _receipts.asStateFlow()

    private val _notifications = MutableStateFlow<List<Notification>>(emptyList())
    val notifications: StateFlow<List<Notification>> = _notifications.asStateFlow()

    private val _workLog = MutableStateFlow<List<WorkLogEntry>>(emptyList())
    val workLog: StateFlow<List<WorkLogEntry>> = _workLog.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    fun loadRequests(residentId: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _requests.value = requestRepository.getAll(residentId)
            _isLoading.value = false
        }
    }

    fun submitRequest(r: Request, residentId: String) {
        viewModelScope.launch {
            requestRepository.save(r)
            _requests.value = requestRepository.getAll(residentId)
        }
    }

    fun loadReceipts(residentId: String) {
        viewModelScope.launch {
            _receipts.value = receiptRepository.getByResident(residentId)
        }
    }

    fun loadNotifications(apartmentNumber: String) {
        viewModelScope.launch {
            _notifications.value = notificationRepository.getAll(apartmentNumber)
        }
    }

    fun loadWorkLog() {
        viewModelScope.launch {
            _workLog.value = workLogRepository.getAll()
        }
    }
}
