package com.example.rest.exceptionHandler;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(CreatorNotFoundException.class)
    public ResponseEntity<Map<String, String>> handleCreatorNotFoundException(CreatorNotFoundException ex) {
        Map<String, String> errorResponse = new HashMap<>();
        errorResponse.put("error", "Creator Not Found");
        errorResponse.put("message", ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidationException(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error ->
                errors.put(error.getField(), error.getDefaultMessage()));
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errors);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<Map<String, String>> handleDataIntegrityViolationException(DataIntegrityViolationException ex) {
        Map<String, String> errorResponse = new HashMap<>();

        // Проверяем, связано ли исключение с уникальностью login
        if (ex.getMessage() != null && ex.getMessage().contains("unique_login")) {
            errorResponse.put("error", "Duplicate Login");
            errorResponse.put("message", "Login already exists");
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(errorResponse); // 403 Forbidden
        }

        // Проверяем, связано ли исключение с уникальностью title
        if (ex.getMessage() != null && ex.getMessage().contains("unique_title")) {
            errorResponse.put("error", "Duplicate Title");
            errorResponse.put("message", "Title already exists");
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(errorResponse); // 403 Forbidden
        }

        // Если ошибка не связана с уникальностью login или title, возвращаем общий ответ
        errorResponse.put("error", "Database Error");
        errorResponse.put("message", "An error occurred while processing your request");
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
    }
}