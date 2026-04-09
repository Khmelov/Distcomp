package com.example.distcomp.exception

import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler

import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException
import org.springframework.http.converter.HttpMessageNotReadableException
import org.springframework.web.client.RestClientResponseException

@ControllerAdvice
class GlobalExceptionHandler {


    @ExceptionHandler(NotFoundException::class)
    fun handleNotFound(ex: NotFoundException): ResponseEntity<ErrorResponse> {
        return ResponseEntity(ErrorResponse(ex.message ?: "Not Found", ERR_NOT_FOUND), HttpStatus.NOT_FOUND)
    }

    @ExceptionHandler(ConflictException::class)
    fun handleConflict(ex: ConflictException): ResponseEntity<ErrorResponse> {
        return ResponseEntity(ErrorResponse(ex.message ?: "Conflict", ERR_FORBIDDEN), HttpStatus.FORBIDDEN)
    }


    @ExceptionHandler(BadRequestException::class)
    fun handleBadRequest(ex: BadRequestException): ResponseEntity<ErrorResponse> {
        return ResponseEntity(ErrorResponse(ex.message ?: "Bad Request", ERR_BAD_REQUEST), HttpStatus.BAD_REQUEST)
    }

    @ExceptionHandler(MethodArgumentNotValidException::class)
    fun handleValidation(ex: MethodArgumentNotValidException): ResponseEntity<ErrorResponse> {
        val message = ex.bindingResult.allErrors.joinToString("; ") { it.defaultMessage ?: "Error" }
        return ResponseEntity(ErrorResponse(message, ERR_VALIDATION), HttpStatus.BAD_REQUEST)
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException::class)
    fun handleTypeMismatch(ex: MethodArgumentTypeMismatchException): ResponseEntity<ErrorResponse> {
        return ResponseEntity(ErrorResponse(ex.message ?: "Type mismatch", ERR_BAD_REQUEST), HttpStatus.BAD_REQUEST)
    }

    @ExceptionHandler(HttpMessageNotReadableException::class)
    fun handleMessageNotReadable(ex: HttpMessageNotReadableException): ResponseEntity<ErrorResponse> {
        return ResponseEntity(ErrorResponse("Message not readable (bad formatting)", ERR_BAD_REQUEST), HttpStatus.BAD_REQUEST)
    }

    @ExceptionHandler(RestClientResponseException::class)
    fun handleRemote(ex: RestClientResponseException): ResponseEntity<String> {
        return ResponseEntity.status(ex.statusCode).body(ex.responseBodyAsString)
    }


    @ExceptionHandler(Exception::class)
    fun handleGeneral(ex: Exception): ResponseEntity<ErrorResponse> {
        return ResponseEntity(ErrorResponse(ex.message ?: "Internal Error", ERR_INTERNAL), HttpStatus.INTERNAL_SERVER_ERROR)
    }

    companion object {
        const val ERR_NOT_FOUND = 40401
        const val ERR_FORBIDDEN = 40301
        const val ERR_BAD_REQUEST = 40001
        const val ERR_VALIDATION = 40002
        const val ERR_INTERNAL = 50001

    }
}
