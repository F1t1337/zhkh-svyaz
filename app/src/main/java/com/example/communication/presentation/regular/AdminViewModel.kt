package com.example.communication.presentation.regular

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.communication.data.models.Notification
import com.example.communication.data.models.NotificationType
import com.example.communication.data.models.Request
import com.example.communication.data.models.RequestStatus
import com.example.communication.data.models.Service
import com.example.communication.data.models.WorkLogEntry
import com.example.communication.data.repositories.INotificationRepository
import com.example.communication.data.repositories.IRequestRepository
import com.example.communication.data.repositories.IServiceRepository
import com.example.communication.data.repositories.IWorkLogRepository
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

private val isoFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())

class AdminViewModel(
    private val requestRepository: IRequestRepository,
    private val notificationRepository: INotificationRepository,
    private val workLogRepository: IWorkLogRepository,
    private val serviceRepository: IServiceRepository
) : ViewModel() {

    private val _requests = MutableStateFlow<List<Request>>(emptyList())
    val requests: StateFlow<List<Request>> = _requests.asStateFlow()

    private val _workLog = MutableStateFlow<List<WorkLogEntry>>(emptyList())
    val workLog: StateFlow<List<WorkLogEntry>> = _workLog.asStateFlow()

    private val _services = MutableStateFlow<List<Service>>(emptyList())
    val services: StateFlow<List<Service>> = _services.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _event = MutableSharedFlow<String>()
    val event: SharedFlow<String> = _event.asSharedFlow()

    fun loadAllRequests() {
        viewModelScope.launch {
            _isLoading.value = true
            _requests.value = requestRepository.getAll()
            _isLoading.value = false
        }
    }

    fun updateRequestStatus(id: String, status: RequestStatus) {
        viewModelScope.launch {
            requestRepository.updateStatus(id, status)
            _requests.value = requestRepository.getAll()
        }
    }

    fun sendNotification(title: String, body: String, targetAll: Boolean, adminId: String) {
        viewModelScope.launch {
            val n = Notification(
                id = System.currentTimeMillis().toString(),
                title = title,
                body = body,
                type = NotificationType.GENERAL,
                targetApartments = if (targetAll) emptyList() else emptyList(),
                sentAt = isoFormat.format(Date()),
                isRead = false
            )
            notificationRepository.send(n)
            _event.emit("Уведомление отправлено!")
        }
    }

    fun loadWorkLog() {
        viewModelScope.launch {
            _workLog.value = workLogRepository.getAll()
        }
    }

    fun addWorkLogEntry(workType: String, description: String, location: String, adminId: String) {
        viewModelScope.launch {
            val entry = WorkLogEntry(
                id = System.currentTimeMillis().toString(),
                workType = workType,
                location = location,
                description = description,
                performedAt = isoFormat.format(Date()),
                reportPdfUrl = "",
                adminId = adminId
            )
            workLogRepository.save(entry)
            _workLog.value = workLogRepository.getAll()
            _event.emit("Запись добавлена!")
        }
    }

    fun loadServices() {
        viewModelScope.launch {
            _services.value = serviceRepository.getAll()
        }
    }

    fun assignService(s: Service) {
        viewModelScope.launch {
            serviceRepository.save(s)
            _services.value = serviceRepository.getAll()
            _event.emit("Услуга назначена!")
        }
    }
}
