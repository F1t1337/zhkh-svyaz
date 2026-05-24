package com.example.communication.data.supabase.dto

import com.example.communication.data.models.User
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ResidentDto(
    val id: String,
    val phone: String,
    val passport: String,
    val password: String? = null,
    @SerialName("apartment_number") val apartmentNumber: String,
    val entrance: String,
    val name: String = ""
)

fun ResidentDto.toDomain() = User.regularUser(
    id = id,
    phone = phone,
    passport = passport,
    password = password,
    apartmentNumber = apartmentNumber,
    entrance = entrance,
    name = name
)
