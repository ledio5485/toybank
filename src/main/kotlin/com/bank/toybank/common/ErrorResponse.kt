package com.bank.toybank.common

import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import java.time.Clock
import java.time.ZonedDateTime
import java.util.UUID

data class ErrorResponse(val status: Int,
                         val error: String,
                         val message: String?,
                         val timestamp: ZonedDateTime = ZonedDateTime.now(Clock.systemUTC()),
                         val requestId: String = UUID.randomUUID().toString()) {

    constructor(status: HttpStatus, message: String?, requestId: String) : this(status.value(), status.reasonPhrase, message, requestId = requestId)
}

class ErrorResponseEntity(body: ErrorResponse) : ResponseEntity<ErrorResponse>(body, HttpStatus.valueOf(body.status)) {

    companion object {
        fun badRequest(message: String?, requestId: String) = clientError(HttpStatus.BAD_REQUEST, message, requestId)
        fun notFound(message: String?, requestId: String) = clientError(HttpStatus.NOT_FOUND, message, requestId)
        private fun clientError(httpStatus: HttpStatus = HttpStatus.BAD_REQUEST, message: String?, requestId: String) = ErrorResponseEntity(
            ErrorResponse(httpStatus, message, requestId)
        )

        fun serverError(message: String?, requestId: String) = serverError(HttpStatus.INTERNAL_SERVER_ERROR, message, requestId)
        fun serverError(httpStatus: HttpStatus = HttpStatus.INTERNAL_SERVER_ERROR, message: String?, requestId: String) = ErrorResponseEntity(
            ErrorResponse(httpStatus, message, requestId)
        )
    }
}