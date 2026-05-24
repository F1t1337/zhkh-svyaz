package com.example.communication.presentation.regular

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.communication.data.models.Receipt
import com.example.communication.data.models.Request
import com.example.communication.data.repositories.IReceiptRepository
import com.example.communication.data.repositories.IRequestRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ResidentViewModel (
    private val requestRepository: IRequestRepository,
    private val receiptRepository: IReceiptRepository
) : ViewModel() {
    private val _requests = MutableStateFlow<List<Request>>(emptyList())
    val requests: StateFlow<List<Request>> = _requests.asStateFlow()

    private val _receipts = MutableStateFlow<List<Receipt>>(emptyList())
    val receipts: StateFlow<List<Receipt>> = _receipts.asStateFlow()
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()
    
    fun loadRequests(residentId: String){
        viewModelScope.launch{
            _isLoading.value = true
            _requests.value = requestRepository.getAll(residentId)
            _isLoading.value = false
        }
    }

    fun submitRequest(r: Request){
        viewModelScope.launch {
            requestRepository.save(r)
        }
    }

    fun loadReceipts(residentId: String){
        viewModelScope.launch {
        _receipts.value = receiptRepository.getByResident(residentId)
        }
    }
}