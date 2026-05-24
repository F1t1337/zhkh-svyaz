package com.example.communication.data.mock

import kotlin.collections.listOf
import com.example.communication.data.models.*

object MockData {

    val regUser1 = User.regularUser(
        id = "1",
        phone = "89603568729",
        passport = "2411222333",
        password = null,
        apartmentNumber = "13",
        entrance = "1"
    )
    val regUser2 = User.regularUser(
        id = "2",
        phone = "89271672730",
        passport = "5269526952",
        password = "52676942",
        apartmentNumber = "67",
        entrance = "3"
    )


    val regUsers = listOf<User.regularUser>(
        regUser1,
        regUser2
    )
    val admUser = User.adminUser(
        id = "1",
        admLogin = "admin",
        password = "admin321",
        permissions = listOf(
            AdminPermission.MANAGE_USERS,
            AdminPermission.EDIT_SETTINGS,
            AdminPermission.VIEW_ANALYTICS
        )
    )

    val admUsers = listOf<User.adminUser>(
        admUser
    )

    val requests = listOf(
        Request(
            id = "1",
            residentId = "1",
            category = RequestCategory.ELECTRICITY,
            description = "Мигает лампочка",
            attachments = emptyList(),
            status = RequestStatus.NEW,
            createdAt = "2026-05-11T10:00:00",
            deadline = "2026-05-15T10:00:00"
        )
    )

    val notifications = listOf(
        Notification(
            id = "1",
            title = "Отключение света",
            body = "С 17 по 19 мая будет отключение света на улице Пушкина дом Колотушкина",
            type = NotificationType.GENERAL,
            targetApartments = emptyList(),
            sentAt = "2026-05-13T09:30:00",
            isRead = false
        )
    )

    val receipts = listOf(
        Receipt(
            id = "1",
            residentId = "2",
            period = "Апрель 2026",
            coldWater = 350.0,
            hotWater = 520.0,
            electricity = 890.0,
            gas = 430.0,
            garbage = 120.0,
            maintenance = 650.0,
            totalAmount = 2960.0,
            isRead = false,
            sentAt = "2026-05-01T09:00:00"
        )
    )

    val services = listOf(
        Service(
            id = "1",
            title = "Замена трубы",
            scheduledAt = "2026-05-15T10:00:00",
            residentId = "1",
            status = ServiceStatus.SCHEDULED
        )
    )

    val workLogEntries = listOf(
        WorkLogEntry(
            id = "1",
            workType = "Сантехника",
            location = "Квартира 13, подъезд 1",
            description = "Замена трубы холодного водоснабжения",
            performedAt = "2026-05-15T12:00:00",
            reportPdfUrl = "",
            adminId = "1"
        )
    )
}



