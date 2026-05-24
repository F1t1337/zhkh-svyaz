package com.example.communication.domain.chain

import com.example.communication.data.models.Request

abstract class RequestValidationHandler {
    private var next: RequestValidationHandler? = null

    fun setNext(handler: RequestValidationHandler): RequestValidationHandler {
        next = handler
        return handler
    }

    fun handle(request: Request): Result<Unit> {
        val result = validate(request)
        if (result.isFailure) return result
        return next?.handle(request) ?: Result.success(Unit)
    }

    protected abstract fun validate(request: Request): Result<Unit>
}

class EmptyDescriptionHandler : RequestValidationHandler() {
    override fun validate(request: Request): Result<Unit> =
        if (request.description.isBlank())
            Result.failure(Exception("Описание не может быть пустым"))
        else Result.success(Unit)
}

class DescriptionLengthHandler(private val minLength: Int = 10) : RequestValidationHandler() {
    override fun validate(request: Request): Result<Unit> =
        if (request.description.length < minLength)
            Result.failure(Exception("Описание слишком короткое (минимум $minLength символов)"))
        else Result.success(Unit)
}

class DuplicateRequestHandler(private val existing: List<Request>) : RequestValidationHandler() {
    override fun validate(request: Request): Result<Unit> {
        val isDuplicate = existing.any {
            it.residentId == request.residentId &&
                it.category == request.category &&
                it.status != com.example.communication.data.models.RequestStatus.DONE &&
                it.status != com.example.communication.data.models.RequestStatus.REJECTED
        }
        return if (isDuplicate)
            Result.failure(Exception("У вас уже есть активное обращение в этой категории"))
        else Result.success(Unit)
    }
}

object RequestValidationChain {
    fun build(existingRequests: List<Request>): RequestValidationHandler {
        val empty = EmptyDescriptionHandler()
        val length = DescriptionLengthHandler()
        val duplicate = DuplicateRequestHandler(existingRequests)
        empty.setNext(length).setNext(duplicate)
        return empty
    }
}
