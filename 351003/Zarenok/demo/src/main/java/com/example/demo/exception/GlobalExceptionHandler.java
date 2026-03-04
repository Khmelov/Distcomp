package com.example.demo.exception;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.crossstore.ChangeSetPersister;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ChangeSetPersister.NotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleNotFound(
            ChangeSetPersister.NotFoundException ex) {

        Map<String, Object> response = new HashMap<>();
        response.put("errorMessage", "Resource not found");
        response.put("errorCode", 40401);

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidation(
            MethodArgumentNotValidException ex) {

        Map<String, Object> response = new HashMap<>();
        response.put("errorMessage", "Validation failed");
        response.put("errorCode", 40001);

        return ResponseEntity.badRequest().body(response);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<Map<String, Object>> handleDuplicate() {

        Map<String, Object> response = new HashMap<>();
        response.put("errorMessage", "Duplicate resource");
        response.put("errorCode", 40301);

        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
    }
}
