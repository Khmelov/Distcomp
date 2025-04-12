package bsuir.dc.publisher.controller

import bsuir.dc.publisher.dto.to.ExceptionResponseTo
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import java.sql.SQLException

@ControllerAdvice
class GlobalExceptionHandler {

    @ExceptionHandler(NoSuchElementException::class)
    fun handleNoSuchElementException(ex: NoSuchElementException): ResponseEntity<ExceptionResponseTo> {
        return ResponseEntity(ExceptionResponseTo(ex.message), HttpStatus.NOT_FOUND)
    }

    @ExceptionHandler(SQLException::class)
    fun handlePSQLException(ex: SQLException): ResponseEntity<ExceptionResponseTo> {
        return ResponseEntity(ExceptionResponseTo(ex.message), HttpStatus.FORBIDDEN)
    }
}
