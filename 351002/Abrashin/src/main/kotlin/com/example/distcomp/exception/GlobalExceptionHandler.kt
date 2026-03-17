package com.example.distcomp.exception

import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler

@ControllerAdvice
class GlobalExceptionHandler {

    @ExceptionHandler(NotFoundException::class)
    fun handleNotFound(ex: NotFoundException): ResponseEntity<ErrorResponse> {
        return ResponseEntity(ErrorResponse(ex.message ?: "Not Found", 404), HttpStatus.NOT_FOUND)
    }

    @ExceptionHandler(ConflictException::class)
    fun handleConflict(ex: ConflictException): ResponseEntity<ErrorResponse> {
        return ResponseEntity(ErrorResponse(ex.message ?: "Conflict", 409), HttpStatus.CONFLICT)
    }

    @ExceptionHandler(BadRequestException::class)
    fun handleBadRequest(ex: BadRequestException): ResponseEntity<ErrorResponse> {
        return ResponseEntity(ErrorResponse(ex.message ?: "Bad Request", 400), HttpStatus.BAD_REQUEST)
    }

    @ExceptionHandler(MethodArgumentNotValidException::class)
    fun handleValidation(ex: MethodArgumentNotValidException): ResponseEntity<ErrorResponse> {
        val message = ex.bindingResult.allErrors.joinToString("; ") { it.defaultMessage ?: "Error" }
        return ResponseEntity(ErrorResponse(message, 400), HttpStatus.BAD_REQUEST)
    }

    @ExceptionHandler(Exception::class)
    fun handleGeneral(ex: Exception): ResponseEntity<ErrorResponse> {
        return ResponseEntity(ErrorResponse(ex.message ?: "Internal Error", 500), HttpStatus.INTERNAL_SERVER_ERROR)
    }
}
