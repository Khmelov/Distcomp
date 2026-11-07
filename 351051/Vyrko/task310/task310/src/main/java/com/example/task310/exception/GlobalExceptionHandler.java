package com.example.task310.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.concurrent.atomic.AtomicInteger;

// Общий обработчик ошибок REST-запросов.
@RestControllerAdvice
public class GlobalExceptionHandler {

    private final AtomicInteger errorCounter = new AtomicInteger(1);

    // Создание пятизначного кода ошибки на основе HTTP-статуса.
    private String createErrorCode(HttpStatus status) {
        return status.value() + String.format("%02d",
                errorCounter.getAndUpdate(value -> value >= 99 ? 1 : value + 1));
    }

    // Обработка ошибок проверки входных данных.
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiError> handleValidationException(
            MethodArgumentNotValidException exception) {

        String message = exception.getBindingResult()
                .getFieldErrors()
                .stream()
                .findFirst()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .orElse("Validation failed");

        HttpStatus status = HttpStatus.BAD_REQUEST;

        return ResponseEntity.status(status)
                .body(new ApiError(message, createErrorCode(status)));
    }

    // Обработка некорректного тела запроса.
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiError> handleNotReadableException(
            HttpMessageNotReadableException exception) {

        HttpStatus status = HttpStatus.BAD_REQUEST;

        return ResponseEntity.status(status)
                .body(new ApiError("Invalid request body", createErrorCode(status)));
    }

    // Обработка попытки использовать занятый логин.
    @ExceptionHandler(DuplicateLoginException.class)
    public ResponseEntity<ApiError> handleDuplicateLoginException(
            DuplicateLoginException exception) {

        HttpStatus status = HttpStatus.CONFLICT;

        return ResponseEntity.status(status)
                .body(new ApiError(exception.getMessage(), createErrorCode(status)));
    }

    // Обработка запросов к отсутствующим ресурсам.
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiError> handleNotFoundException(
            ResourceNotFoundException exception) {

        HttpStatus status = HttpStatus.NOT_FOUND;

        return ResponseEntity.status(status)
                .body(new ApiError(exception.getMessage(), createErrorCode(status)));
    }

    // Обработка остальных непредвиденных ошибок.
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiError> handleGeneralException(Exception exception) {
        HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;

        return ResponseEntity.status(status)
                .body(new ApiError("Internal server error", createErrorCode(status)));
    }
}