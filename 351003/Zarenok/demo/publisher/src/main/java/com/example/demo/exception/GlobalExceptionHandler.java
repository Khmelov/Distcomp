package com.example.demo.exception;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.ErrorResponse;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.validation.ObjectError;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.util.stream.Collectors;

import java.util.HashMap;
import java.util.Map;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.NOT_FOUND;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleNotFound(NotFoundException ex) {
        return buildResponse(404, ex.getMessage(), 40401);
    }

    @ExceptionHandler(DuplicateException.class)
    public ResponseEntity<Map<String, Object>> handleDuplicate(DuplicateException ex) {
        return buildResponse(403, ex.getMessage(), 40301);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidation(MethodArgumentNotValidException ex) {
        String message = ex.getBindingResult().getAllErrors().stream()
                .map(ObjectError::getDefaultMessage)
                .collect(Collectors.joining(", "));
        return buildResponse(400, message, 40001);
    }
    
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<Map<String, Object>> handleDataIntegrity(DataIntegrityViolationException ex) {
        String message = ex.getMostSpecificCause().getMessage().toLowerCase();
        Map<String, Object> response = new HashMap<>();
        if (message.contains("foreign key")) {
            response.put("errorMessage", "Referenced entity not found");
            response.put("errorCode", 40001);
            return ResponseEntity.badRequest().body(response);
        } else if (message.contains("unique constraint") || message.contains("duplicate key")) {
            response.put("errorMessage", "Duplicate resource");
            response.put("errorCode", 40301);
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
        } else if (message.contains("not null")) {
            response.put("errorMessage", "Missing required field");
            response.put("errorCode", 40002); // или 40001
            return ResponseEntity.badRequest().body(response);
        }
        response.put("errorMessage", "Database error");
        response.put("errorCode", 50001);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }



    private ResponseEntity<Map<String, Object>> buildResponse(int status, String message, int code) {
        Map<String, Object> response = new HashMap<>();
        response.put("errorMessage", message);
        response.put("errorCode", code);
        return ResponseEntity.status(status).body(response);
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<Map<String, Object>> handleMethodArgumentTypeMismatch(MethodArgumentTypeMismatchException ex) {
        Map<String, Object> error = new HashMap<>();
        if ("id".equals(ex.getName()) || "issueId".equals(ex.getName())) {
            error.put("status", HttpStatus.NOT_FOUND.value());
            error.put("message", "Resource not found");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
        }
        error.put("status", HttpStatus.BAD_REQUEST.value());
        error.put("message", "Invalid parameter");
        return ResponseEntity.badRequest().body(error);
    }
}
