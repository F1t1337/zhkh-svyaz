package com.example.communication.presentation.regular

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.communication.data.models.Notification
import com.example.communication.data.models.NotificationType
import com.example.communication.data.models.Request
import com.example.communication.data.models.RequestStatus
import com.example.communication.data.models.Service
import com.example.communication.data.models.WorkLogEntry
import com.example.communication.data.repositories.AuthRepository
import com.example.communication.data.repositories.INotificationRepository
import com.example.communication.data.repositories.IRequestRepository
import com.example.communication.data.repositories.IServiceRepository
import com.example.communication.data.repositories.ISettingsRepository
import com.example.communication.data.repositories.IWorkLogRepository
import com.example.communication.domain.decorator.buildNotificationSender
import com.example.communication.domain.state.RequestStateManager
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
    private val serviceRepository: IServiceRepository,
    private val settingsRepository: ISettingsRepository,
    private val authRepository: AuthRepository
) : ViewModel() {

    // Decorator: отправка уведомлений с логированием и повторными попытками
    private val notificationSender = buildNotificationSender(notificationRepository)

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

    private val _messengerUrl = MutableStateFlow("")
    val messengerUrl: StateFlow<String> = _messengerUrl.asStateFlow()

    private val _requestCount = MutableStateFlow(0)
    val requestCount: StateFlow<Int> = _requestCount.asStateFlow()

    private val _residentCount = MutableStateFlow(0)
    val residentCount: StateFlow<Int> = _residentCount.asStateFlow()

    fun loadAllRequests() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                _requests.value = requestRepository.getAll()
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun loadStats() {
        viewModelScope.launch {
            _requestCount.value = requestRepository.countAll()
            _residentCount.value = authRepository.countResidents()
            _messengerUrl.value = settingsRepository.get("messenger_url") ?: ""
        }
    }

    fun loadMessengerUrl() {
        viewModelScope.launch {
            _messengerUrl.value = settingsRepository.get("messenger_url") ?: ""
        }
    }

    fun saveMessengerUrl(url: String) {
        viewModelScope.launch {
            settingsRepository.set("messenger_url", url)
            _messengerUrl.value = url
            _event.emit("Ссылка сохранена!")
        }
    }

    fun updateRequestStatus(id: String, newStatus: RequestStatus) {
        viewModelScope.launch {
            val current = _requests.value.find { it.id == id }?.status ?: return@launch
            val transition = RequestStateManager.transition(current, newStatus)
            if (transition.isFailure) {
                _event.emit(transition.exceptionOrNull()?.message ?: "Недопустимый переход")
                return@launch
            }
            requestRepository.updateStatus(id, newStatus)
            _requests.value = requestRepository.getAll()
        }
    }

    fun replyToRequest(id: String, response: String) {
        viewModelScope.launch {
            requestRepository.updateAdminResponse(id, response)
            _requests.value = requestRepository.getAll()
            _event.emit("Ответ отправлен!")
        }
    }

    fun availableStatuses(current: RequestStatus): List<RequestStatus> =
        RequestStateManager.availableFrom(current).toList()

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
            val sent = notificationSender.send(n)
            _event.emit(if (sent) "Уведомление отправлено!" else "Ошибка отправки")
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
            _isLoading.value = true
            try {
                _services.value = serviceRepository.getAll()
            } finally {
                _isLoading.value = false
            }
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
