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
import com.example.communication.data.repositories.ISettingsRepository
import com.example.communication.data.repositories.IWorkLogRepository
import com.example.communication.domain.chain.RequestValidationChain
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ResidentViewModel(
    private val requestRepository: IRequestRepository,
    private val receiptRepository: IReceiptRepository,
    private val notificationRepository: INotificationRepository,
    private val workLogRepository: IWorkLogRepository,
    private val settingsRepository: ISettingsRepository
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

    private val _error = MutableSharedFlow<String>()
    val error: SharedFlow<String> = _error.asSharedFlow()

    private val _messengerUrl = MutableStateFlow<String>("")
    val messengerUrl: StateFlow<String> = _messengerUrl.asStateFlow()

    fun loadAll(residentId: String, apartmentNumber: String) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                _requests.value = requestRepository.getAll(residentId)
                _receipts.value = receiptRepository.getByResident(residentId)
                _notifications.value = notificationRepository.getAll(apartmentNumber)
                _workLog.value = workLogRepository.getAll()
                _messengerUrl.value = settingsRepository.get("messenger_url") ?: ""
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun loadRequests(residentId: String) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                _requests.value = requestRepository.getAll(residentId)
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun submitRequest(r: Request, residentId: String) {
        viewModelScope.launch {
            val chain = RequestValidationChain.build(_requests.value)
            val validation = chain.handle(r)
            if (validation.isFailure) {
                _error.emit(validation.exceptionOrNull()?.message ?: "Ошибка валидации")
                return@launch
            }
            requestRepository.save(r)
            _requests.value = requestRepository.getAll(residentId)
        }
    }

    fun loadReceipts(residentId: String) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                _receipts.value = receiptRepository.getByResident(residentId)
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun loadNotifications(apartmentNumber: String) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                _notifications.value = notificationRepository.getAll(apartmentNumber)
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun loadWorkLog() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                _workLog.value = workLogRepository.getAll()
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun loadMessengerUrl() {
        viewModelScope.launch {
            _messengerUrl.value = settingsRepository.get("messenger_url") ?: ""
        }
    }

    fun markNotificationRead(id: String, apartmentNumber: String) {
        viewModelScope.launch {
            notificationRepository.markRead(id)
            _notifications.value = notificationRepository.getAll(apartmentNumber)
        }
    }

    fun markReceiptRead(id: String, residentId: String) {
        viewModelScope.launch {
            receiptRepository.markRead(id)
            _receipts.value = receiptRepository.getByResident(residentId)
        }
    }
}
