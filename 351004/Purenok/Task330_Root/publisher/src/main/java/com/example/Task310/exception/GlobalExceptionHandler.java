package com.example.Task310.exception;

import com.example.Task310.dto.ErrorResponseTo;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    // Тесты 2, 7, 14, 20 -> 40001



    
    // ТЕСТЫ 3 и 9 -> ДОЛЖЕН БЫТЬ 40301
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponseTo> handleValidation(MethodArgumentNotValidException ex) {
        // Тест 7 ожидает 400
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ErrorResponseTo("Validation failed", "40001"));
    }

    @ExceptionHandler(AlreadyExistsException.class)
    public ResponseEntity<ErrorResponseTo> handleAlreadyExists(AlreadyExistsException ex) {
        // Тесты 3 и 9 ждут 40301
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(new ErrorResponseTo(ex.getMessage(), "40301"));
    }

    // ТЕСТЫ 8 и 15 -> 40401
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponseTo> handleNotFound(ResourceNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new ErrorResponseTo(ex.getMessage(), "40401"));
    }
}