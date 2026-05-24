package com.example.communication.domain.state

import com.example.communication.data.models.RequestStatus

object RequestStateManager {

    private val allowedTransitions: Map<RequestStatus, Set<RequestStatus>> = mapOf(
        RequestStatus.NEW        to setOf(RequestStatus.IN_PROGRESS, RequestStatus.REJECTED),
        RequestStatus.IN_PROGRESS to setOf(RequestStatus.DONE, RequestStatus.REJECTED),
        RequestStatus.DONE       to emptySet(),
        RequestStatus.REJECTED   to emptySet()
    )

    fun canTransition(from: RequestStatus, to: RequestStatus): Boolean =
        allowedTransitions[from]?.contains(to) == true

    fun availableFrom(current: RequestStatus): Set<RequestStatus> =
        allowedTransitions[current] ?: emptySet()

    fun transition(current: RequestStatus, to: RequestStatus): Result<RequestStatus> =
        if (canTransition(current, to))
            Result.success(to)
        else
            Result.failure(Exception("Переход из «${current.label}» в «${to.label}» недопустим"))

    private val RequestStatus.label get() = when (this) {
        RequestStatus.NEW         -> "Принято"
        RequestStatus.IN_PROGRESS -> "В работе"
        RequestStatus.DONE        -> "Выполнено"
        RequestStatus.REJECTED    -> "Отклонено"
    }
}
