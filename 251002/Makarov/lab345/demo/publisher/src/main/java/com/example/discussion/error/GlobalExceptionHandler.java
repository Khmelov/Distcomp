package com.example.discussion.error;

import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.ValidationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

@ControllerAdvice
public class GlobalExceptionHandler {

    // Обработка ошибки EntityNotFoundException
    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleEntityNotFound(EntityNotFoundException ex) {
        ErrorResponse errorResponse = new ErrorResponse("Entity not found", "40401");
        return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
    }

    // Обработка ошибки IllegalArgumentException
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleBadRequest(IllegalArgumentException ex) {
        ErrorResponse errorResponse = new ErrorResponse("Invalid argument", "40001");
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    // Обработка ошибки NullPointerException
    @ExceptionHandler(NullPointerException.class)
    public ResponseEntity<ErrorResponse> handleNullPointerException(NullPointerException ex) {
        ErrorResponse errorResponse = new ErrorResponse("Null pointer exception", "50001");
        return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    // Обработка ошибки MethodArgumentNotValidException (например, валидация объектов)
    @ExceptionHandler(org.springframework.validation.BindException.class)
    public ResponseEntity<ErrorResponse> handleValidationError(org.springframework.validation.BindException ex) {
        ErrorResponse errorResponse = new ErrorResponse("Validation error", "40002");
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    // Обработка ошибки доступа (например, Unauthorized)
    @ExceptionHandler(SecurityException.class)
    public ResponseEntity<ErrorResponse> handleSecurityException(SecurityException ex) {
        ErrorResponse errorResponse = new ErrorResponse("Access denied", "40101");
        return new ResponseEntity<>(errorResponse, HttpStatus.UNAUTHORIZED);
    }

    // Обработка ошибки ResourceAlreadyExistsException (например, когда ресурс уже существует)
    @ExceptionHandler(ResourceAlreadyExistsException.class)
    public ResponseEntity<ErrorResponse> handleResourceAlreadyExists(ResourceAlreadyExistsException ex) {
        ErrorResponse errorResponse = new ErrorResponse("Resource already exists", "40901");
        return new ResponseEntity<>(errorResponse, HttpStatus.CONFLICT);
    }

    // Обработка остальных исключений
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGenericException(Exception ex) {
        ErrorResponse errorResponse = new ErrorResponse("An unexpected error occurred", "50000");
        return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ErrorResponse> handleTypeMismatch(MethodArgumentTypeMismatchException ex) {
        ErrorResponse errorResponse = new ErrorResponse("Invalid argument type", "40002");
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }
    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<String> handleValidationException(ValidationException e) {
        return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
    }
    @ExceptionHandler(TitleAlreadyExistsException.class)
    public ResponseEntity<String> handleTitleExists(TitleAlreadyExistsException ex) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(ex.getMessage());
    }
    @ExceptionHandler( DuplicateLoginException.class)
    public ResponseEntity<ErrorResponse> handleExceptions(Exception ex) {
        ErrorResponse errorResponse = new ErrorResponse(ex.getMessage(), "123123123123");
        return new ResponseEntity<>(errorResponse, HttpStatus.FORBIDDEN);
    }
}
