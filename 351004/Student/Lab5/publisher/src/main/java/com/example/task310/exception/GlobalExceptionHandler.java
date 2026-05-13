package com.example.task310.exception;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {

    // 404 объект не найден
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ErrorResponseTo> handleNotFound(RuntimeException e) {
        e.printStackTrace();
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new ErrorResponseTo(e.getMessage(), 40401));
    }

    // 400 короткий текст
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponseTo> handleValidation(MethodArgumentNotValidException e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ErrorResponseTo("Validation failed", 40001));
    }

    // 403 дубликат
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ErrorResponseTo> handleConflict(DataIntegrityViolationException e) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(new ErrorResponseTo("Data integrity violation", 40301));
    }
}