package com.example.communication.data.models

enum class ServiceStatus {
    SCHEDULED,
    IN_PROGRESS,
    COMPLETED,
    CANCELLED
}

data class Service(
    val id: String,
    /** Тип услуги из предопределённого списка (Плановый осмотр, Замена труб…) */
    val serviceType: String,
    /** Дополнительное описание/примечание */
    val description: String = "",
    val scheduledAt: String,
    val residentId: String,
    /** Номер квартиры — сохраняется при создании для отображения */
    val apartmentNumber: String = "",
    val status: ServiceStatus
)
