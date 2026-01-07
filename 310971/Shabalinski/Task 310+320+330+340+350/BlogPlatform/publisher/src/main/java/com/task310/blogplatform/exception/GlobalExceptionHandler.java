package com.task310.blogplatform.exception;

import com.task310.blogplatform.dto.ErrorResponseTo;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.reactive.function.client.WebClientException;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<ErrorResponseTo> handleEntityNotFoundException(EntityNotFoundException ex) {
        ErrorResponseTo error = new ErrorResponseTo(ex.getMessage(), "40401");
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }

    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<ErrorResponseTo> handleValidationException(ValidationException ex) {
        ErrorResponseTo error = new ErrorResponseTo(ex.getMessage(), "40001");
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    @ExceptionHandler(DuplicateException.class)
    public ResponseEntity<ErrorResponseTo> handleDuplicateException(DuplicateException ex) {
        ErrorResponseTo error = new ErrorResponseTo(ex.getMessage(), "40301");
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(error);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ErrorResponseTo> handleDataIntegrityViolationException(DataIntegrityViolationException ex) {
        String message = ex.getMessage();
        if (message != null && message.contains("duplicate key")) {
            // Extract field name from error message
            String fieldName = "value";
            if (message.contains("tbl_user_login")) {
                fieldName = "login";
            } else if (message.contains("tbl_article_title")) {
                fieldName = "title";
            } else if (message.contains("tbl_label_name")) {
                fieldName = "name";
            }
            ErrorResponseTo error = new ErrorResponseTo("Duplicate " + fieldName + " value", "40301");
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(error);
        }
        ErrorResponseTo error = new ErrorResponseTo("Data integrity violation: " + ex.getMessage(), "40001");
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ErrorResponseTo> handleMethodArgumentTypeMismatch(MethodArgumentTypeMismatchException ex) {
        String message = "Invalid parameter type: " + ex.getName() + " must be a valid number";
        ErrorResponseTo error = new ErrorResponseTo(message, "40001");
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    @ExceptionHandler(WebClientException.class)
    public ResponseEntity<ErrorResponseTo> handleWebClientException(WebClientException ex) {
        String message = ex.getMessage();
        if (message != null && message.contains("Connection refused")) {
            ErrorResponseTo error = new ErrorResponseTo("Discussion service is not available. Please ensure the discussion module is running on port 24130.", "50301");
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(error);
        }
        ErrorResponseTo error = new ErrorResponseTo("Error communicating with discussion service: " + message, "50001");
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
    }

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ErrorResponseTo> handleAuthenticationException(AuthenticationException ex) {
        ErrorResponseTo error = new ErrorResponseTo("Authentication failed: " + ex.getMessage(), "40101");
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ErrorResponseTo> handleBadCredentialsException(BadCredentialsException ex) {
        ErrorResponseTo error = new ErrorResponseTo("Invalid login or password", "40102");
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErrorResponseTo> handleAccessDeniedException(AccessDeniedException ex) {
        ErrorResponseTo error = new ErrorResponseTo("Access denied: " + ex.getMessage(), "40302");
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(error);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponseTo> handleGenericException(Exception ex) {
        // Check if it's a WebClientException wrapped in RuntimeException
        Throwable cause = ex.getCause();
        if (cause instanceof WebClientException) {
            return handleWebClientException((WebClientException) cause);
        }
        // Check if it's a Kafka-related error
        String message = ex.getMessage();
        if (message != null && (message.contains("kafka") || message.contains("Kafka") || 
            message.contains("Connection refused") || message.contains("Bootstrap broker"))) {
            ErrorResponseTo error = new ErrorResponseTo("Kafka is not available. Please ensure Kafka is running on localhost:9092. See KAFKA_SETUP.md for instructions.", "50302");
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(error);
        }
        ErrorResponseTo error = new ErrorResponseTo("Internal server error: " + ex.getMessage(), "50001");
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
    }
}

