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
        entrance = "1",
        name = "Иван Петров"
    )
    val regUser2 = User.regularUser(
        id = "2",
        phone = "89271672730",
        passport = "5269526952",
        password = "52676942",
        apartmentNumber = "67",
        entrance = "3",
        name = "Мария Кузнецова"
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

    val requests = mutableListOf(
        Request(
            id = "1",
            residentId = "1",
            category = RequestCategory.ELECTRICITY,
            description = "Мигает лампочка в подъезде на 3 этаже, уже третий день",
            attachments = emptyList(),
            status = RequestStatus.NEW,
            createdAt = "2026-05-20T10:00:00",
            deadline = "2026-05-27T10:00:00",
            apartmentNumber = "13"
        ),
        Request(
            id = "2",
            residentId = "1",
            category = RequestCategory.PLUMBING,
            description = "Течёт кран в ванной, капает постоянно",
            attachments = emptyList(),
            status = RequestStatus.IN_PROGRESS,
            createdAt = "2026-05-15T14:30:00",
            deadline = "2026-05-22T14:30:00",
            apartmentNumber = "13"
        ),
        Request(
            id = "3",
            residentId = "2",
            category = RequestCategory.REPAIR,
            description = "Трещина на стене в коридоре",
            attachments = emptyList(),
            status = RequestStatus.DONE,
            createdAt = "2026-05-01T09:00:00",
            deadline = "2026-05-10T09:00:00",
            apartmentNumber = "67"
        )
    )

    val notifications = listOf(
        Notification(
            id = "1",
            title = "Плановое отключение света",
            body = "25 мая с 10:00 до 14:00 будет плановое отключение электроэнергии в связи с ремонтными работами",
            type = NotificationType.GENERAL,
            targetApartments = emptyList(),
            sentAt = "2026-05-23T09:00:00",
            isRead = false
        ),
        Notification(
            id = "2",
            title = "Ваше обращение принято",
            body = "Обращение №1 «Мигает лампочка» принято в работу. Ожидаемый срок устранения: 27 мая",
            type = NotificationType.REQUEST_UPDATE,
            targetApartments = emptyList(),
            sentAt = "2026-05-20T11:00:00",
            isRead = false
        ),
        Notification(
            id = "3",
            title = "Квитанция за май",
            body = "Выставлена квитанция за май 2026. Сумма к оплате: 3090 ₽",
            type = NotificationType.RECEIPT,
            targetApartments = emptyList(),
            sentAt = "2026-05-01T09:00:00",
            isRead = true
        )
    )

    val receipts = listOf(
        Receipt(
            id = "1",
            residentId = "1",
            period = "Май 2026",
            coldWater = 380.0,
            hotWater = 540.0,
            electricity = 920.0,
            gas = 450.0,
            garbage = 120.0,
            maintenance = 680.0,
            totalAmount = 3090.0,
            isRead = false,
            sentAt = "2026-05-01T09:00:00"
        ),
        Receipt(
            id = "2",
            residentId = "1",
            period = "Апрель 2026",
            coldWater = 350.0,
            hotWater = 520.0,
            electricity = 890.0,
            gas = 430.0,
            garbage = 120.0,
            maintenance = 650.0,
            totalAmount = 2960.0,
            isRead = true,
            sentAt = "2026-04-01T09:00:00"
        ),
        Receipt(
            id = "3",
            residentId = "2",
            period = "Май 2026",
            coldWater = 310.0,
            hotWater = 480.0,
            electricity = 750.0,
            gas = 390.0,
            garbage = 120.0,
            maintenance = 650.0,
            totalAmount = 2700.0,
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



