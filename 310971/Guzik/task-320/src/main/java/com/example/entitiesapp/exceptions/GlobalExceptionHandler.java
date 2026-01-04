package com.example.entitiesapp.exceptions;

import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import com.fasterxml.jackson.databind.exc.MismatchedInputException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponse> handleHttpMessageNotReadable(HttpMessageNotReadableException ex) {
        String errorMessage;

        // Извлекаем корневую причину
        Throwable cause = ex.getCause();

        if (cause instanceof IllegalArgumentException) {
            // Наше сообщение из конструктора ArticleRequestTo
            errorMessage = cause.getMessage();
        } else if (cause instanceof InvalidFormatException) {
            // Ошибка формата Jackson
            InvalidFormatException ife = (InvalidFormatException) cause;
            errorMessage = "Invalid value for field '" + ife.getPath().get(0).getFieldName() + "': " + ife.getValue();
        } else if (cause instanceof MismatchedInputException) {
            // Несоответствие типа
            errorMessage = "Type mismatch in request body";
        } else {
            // Общая ошибка
            errorMessage = "Invalid request body: " + (cause != null ? cause.getMessage() : ex.getMessage());
        }

        int errorCode = HttpStatus.BAD_REQUEST.value() * 100 + 3;
        ErrorResponse error = new ErrorResponse(
                errorCode,
                errorMessage,
                LocalDateTime.now()
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleResourceNotFound(ResourceNotFoundException ex) {
        int errorCode = HttpStatus.NOT_FOUND.value() * 100 + 1;
        ErrorResponse error = new ErrorResponse(
                errorCode,
                ex.getMessage(),
                LocalDateTime.now()
        );
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }

    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<ErrorResponse> handleValidationException(ValidationException ex) {
        int errorCode = HttpStatus.BAD_REQUEST.value() * 100 + 1;
        ErrorResponse error = new ErrorResponse(
                errorCode,
                ex.getMessage(),
                LocalDateTime.now()
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    @ExceptionHandler(DuplicateResourceException.class)
    public ResponseEntity<ErrorResponse> handleDuplicateResource(DuplicateResourceException ex) {
        int errorCode = HttpStatus.FORBIDDEN.value() * 100 + 1;
        ErrorResponse error = new ErrorResponse(
                errorCode,
                ex.getMessage(),
                LocalDateTime.now()
        );
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(error);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationExceptions(
            MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error ->
                errors.put(error.getField(), error.getDefaultMessage()));

        int errorCode = HttpStatus.BAD_REQUEST.value() * 100 + 2;
        ErrorResponse error = new ErrorResponse(
                errorCode,
                "Validation failed: " + errors,
                LocalDateTime.now()
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleIllegalArgument(IllegalArgumentException ex) {
        int errorCode = HttpStatus.BAD_REQUEST.value() * 100 + 4;
        ErrorResponse error = new ErrorResponse(
                errorCode,
                ex.getMessage(),
                LocalDateTime.now()
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGenericException(Exception ex) {
        int errorCode = HttpStatus.INTERNAL_SERVER_ERROR.value() * 100 + 1;
        ErrorResponse error = new ErrorResponse(
                errorCode,
                "Internal server error: " + ex.getMessage(),
                LocalDateTime.now()
        );
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
    }
}