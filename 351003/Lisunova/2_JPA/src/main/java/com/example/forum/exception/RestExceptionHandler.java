package com.example.forum.exception;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import jakarta.validation.ConstraintViolationException;

@ControllerAdvice
public class RestExceptionHandler {

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<ApiError> handleNotFound(NotFoundException ex) {
        ApiError error = new ApiError(ex.getMessage(), ex.getErrorCode());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }

    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<ApiError> handleBadRequest(BadRequestException ex) {
        ApiError error = new ApiError(ex.getMessage(), ex.getErrorCode());

        // Если код ошибки начинается с 403, возвращаем 403 статус
        if (ex.getErrorCode() != null && ex.getErrorCode().startsWith("403")) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(error);
        }

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiError> handleIllegalArgument(IllegalArgumentException ex) {
        ApiError error = new ApiError(ex.getMessage(), "40099");
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiError> handleValidationExceptions(MethodArgumentNotValidException ex) {
        String errorMessage = ex.getBindingResult().getAllErrors().get(0).getDefaultMessage();
        ApiError error = new ApiError(errorMessage, "40001");
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ApiError> handleConstraintViolation(ConstraintViolationException ex) {
        ApiError error = new ApiError(ex.getMessage(), "40002");
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ApiError> handleDataIntegrityViolation(DataIntegrityViolationException ex) {
        String message = ex.getMessage();
        String errorCode = "50001";
        HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;

        if (message.contains("unique constraint") || message.contains("duplicate key")) {
            if (message.contains("login")) {
                message = "Login already exists";
                errorCode = "40301";
                status = HttpStatus.FORBIDDEN;
            } else if (message.contains("name")) { // Обработка дубликата имени метки
                message = "Mark with this name already exists";
                errorCode = "40321";
                status = HttpStatus.FORBIDDEN;
            }
        }
        // Обработка удаления метки, которая используется в tbl_topic_mark
        else if (message.contains("foreign key") || message.contains("referenced")) {
            message = "Record is in use and cannot be deleted";
            errorCode = "40321";
            status = HttpStatus.FORBIDDEN;
        }

        ApiError error = new ApiError(message, errorCode);
        return ResponseEntity.status(status).body(error);
    }

    // Добавь этот метод в RestExceptionHandler.java [cite: 675]
    @ExceptionHandler(ForbiddenException.class)
    public ResponseEntity<ApiError> handleForbidden(ForbiddenException e) {
        ApiError error = new ApiError(e.getMessage(), "40301");
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(error);
    }
}