package com.distcomp.controller

import com.distcomp.dto.ErrorResponseTo
import com.distcomp.exception.AbstractException
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice

@RestControllerAdvice
class GlobalExceptionHandler {
    @ExceptionHandler(AbstractException::class)
    fun handleUserNotFound(e: AbstractException): ResponseEntity<ErrorResponseTo> {
        val error = ErrorResponseTo(
            status = e.errorCode.value(),
            message = e.errorMsg
        )
        return ResponseEntity.status(e.errorCode).body(error)
    }
}