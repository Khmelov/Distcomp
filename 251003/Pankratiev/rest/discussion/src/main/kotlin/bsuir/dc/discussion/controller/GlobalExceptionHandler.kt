package bsuir.dc.discussion.controller

import bsuir.dc.discussion.dto.to.ExceptionResponseTo
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler

@ControllerAdvice
class GlobalExceptionHandler {
    @ExceptionHandler(NoSuchElementException::class)
    fun handleNoSuchElementException(ex: NoSuchElementException): ResponseEntity<ExceptionResponseTo> {
        return ResponseEntity(ExceptionResponseTo(ex.message), HttpStatus.NOT_FOUND)
    }
}
