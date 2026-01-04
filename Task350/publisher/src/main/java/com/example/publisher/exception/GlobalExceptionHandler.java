package com.example.publisher.exception;

import jakarta.validation.ConstraintViolationException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ApiError> handleDataIntegrity(DataIntegrityViolationException ex) {
        String msg = ex.getMessage().toLowerCase();
        if (msg.contains("login")) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(new ApiError("Login already exists", 40902));
        }
        if (msg.contains("title") && msg.contains("user_id")) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(new ApiError("Story title must be unique per user", 40903));
        }
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ApiError("Database error", 50001));
    }

    @ExceptionHandler(AppException.class)
    public ResponseEntity<ApiError> handleAppException(AppException ex) {
        return ResponseEntity.status(getHttpStatus(ex.getErrorCode()))
                .body(new ApiError(ex.getMessage(), ex.getErrorCode()));
    }

    @ExceptionHandler({MethodArgumentNotValidException.class, ConstraintViolationException.class})
    public ResponseEntity<ApiError> handleValidation(Exception ex) {
        String message = ex instanceof MethodArgumentNotValidException m ?
                m.getBindingResult().getFieldErrors().stream()
                        .map(e -> e.getField() + ": " + e.getDefaultMessage())
                        .collect(Collectors.joining("; ")) :
                ex.getMessage();
        return ResponseEntity.badRequest()
                .body(new ApiError("Validation failed: " + message, 40000));
    }

    private HttpStatus getHttpStatus(int errorCode) {
        int httpCode = errorCode / 100;
        return switch (httpCode) {
            case 400 -> HttpStatus.BAD_REQUEST;
            case 401 -> HttpStatus.UNAUTHORIZED;
            case 403, 409 -> HttpStatus.CONFLICT;
            case 404 -> HttpStatus.NOT_FOUND;
            case 500 -> HttpStatus.INTERNAL_SERVER_ERROR;
            default -> HttpStatus.INTERNAL_SERVER_ERROR;
        };
    }
}