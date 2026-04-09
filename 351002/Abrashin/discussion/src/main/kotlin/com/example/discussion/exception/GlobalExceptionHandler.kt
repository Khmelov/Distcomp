package com.example.discussion.exception

import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler

@ControllerAdvice
class GlobalExceptionHandler {
    @ExceptionHandler(NotFoundException::class)
    fun handleNotFound(ex: NotFoundException): ResponseEntity<ErrorResponse> =
        ResponseEntity(ErrorResponse(ex.message ?: "Not Found", ERR_NOT_FOUND), HttpStatus.NOT_FOUND)

    @ExceptionHandler(BadRequestException::class)
    fun handleBadRequest(ex: BadRequestException): ResponseEntity<ErrorResponse> =
        ResponseEntity(ErrorResponse(ex.message ?: "Bad Request", ERR_BAD_REQUEST), HttpStatus.BAD_REQUEST)

    @ExceptionHandler(MethodArgumentNotValidException::class)
    fun handleValidation(ex: MethodArgumentNotValidException): ResponseEntity<ErrorResponse> {
        val message = ex.bindingResult.allErrors.joinToString("; ") { it.defaultMessage ?: "Error" }
        return ResponseEntity(ErrorResponse(message, ERR_VALIDATION), HttpStatus.BAD_REQUEST)
    }

    @ExceptionHandler(Exception::class)
    fun handleGeneral(ex: Exception): ResponseEntity<ErrorResponse> =
        ResponseEntity(
            ErrorResponse(ex.message ?: "Internal Error", ERR_INTERNAL),
            HttpStatus.INTERNAL_SERVER_ERROR
        )

    companion object {
        const val ERR_NOT_FOUND = 40401
        const val ERR_BAD_REQUEST = 40001
        const val ERR_VALIDATION = 40002
        const val ERR_INTERNAL = 50001
    }
}
