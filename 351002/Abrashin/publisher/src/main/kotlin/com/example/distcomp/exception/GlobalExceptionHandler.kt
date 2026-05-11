package com.example.distcomp.exception

import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.access.AccessDeniedException
import org.springframework.security.core.AuthenticationException
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException
import org.springframework.http.converter.HttpMessageNotReadableException
import org.springframework.web.client.RestClientResponseException
import org.springframework.web.servlet.resource.NoResourceFoundException

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

    @ExceptionHandler(AuthenticationException::class)
    fun handleAuthentication(ex: AuthenticationException): ResponseEntity<ErrorResponse> {
        return ResponseEntity(ErrorResponse(ex.message ?: "Unauthorized", ERR_UNAUTHORIZED), HttpStatus.UNAUTHORIZED)
    }

    @ExceptionHandler(AccessDeniedException::class)
    fun handleAccessDenied(ex: AccessDeniedException): ResponseEntity<ErrorResponse> {
        return ResponseEntity(ErrorResponse(ex.message ?: "Forbidden", ERR_FORBIDDEN), HttpStatus.FORBIDDEN)
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

    @ExceptionHandler(RemoteDiscussionException::class)
    fun handleRemoteDiscussion(ex: RemoteDiscussionException): ResponseEntity<ErrorResponse> {
        return ResponseEntity.status(ex.status).body(
            ErrorResponse(ex.message ?: "Remote error", ex.remoteCode ?: ERR_INTERNAL)
        )
    }

    @ExceptionHandler(NoteRequestTimeoutException::class)
    fun handleNoteTimeout(ex: NoteRequestTimeoutException): ResponseEntity<ErrorResponse> {
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
            .body(ErrorResponse(ex.message ?: "Note request timed out", ERR_SERVICE_UNAVAILABLE))
    }

    @ExceptionHandler(NoResourceFoundException::class)
    fun handleNoResourceFound(ex: NoResourceFoundException): ResponseEntity<ErrorResponse> {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
            .body(ErrorResponse(ex.message ?: "Not Found", ERR_NOT_FOUND))
    }

    @ExceptionHandler(Exception::class)
    fun handleGeneral(ex: Exception): ResponseEntity<ErrorResponse> {
        return ResponseEntity(ErrorResponse(ex.message ?: "Internal Error", ERR_INTERNAL), HttpStatus.INTERNAL_SERVER_ERROR)
    }

    companion object {
        const val ERR_UNAUTHORIZED = 40101
        const val ERR_NOT_FOUND = 40401
        const val ERR_FORBIDDEN = 40301
        const val ERR_BAD_REQUEST = 40001
        const val ERR_VALIDATION = 40002
        const val ERR_SERVICE_UNAVAILABLE = 50301
        const val ERR_INTERNAL = 50001

    }
}
