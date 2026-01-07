package com.task310.discussion.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<ErrorResponseTo> handleEntityNotFound(EntityNotFoundException ex) {
        logger.warn("EntityNotFoundException: {}", ex.getMessage());
        ErrorResponseTo error = new ErrorResponseTo("40401", ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }

    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<ErrorResponseTo> handleValidation(ValidationException ex) {
        logger.warn("ValidationException: {}", ex.getMessage());
        ErrorResponseTo error = new ErrorResponseTo("40001", ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponseTo> handleGeneric(Exception ex) {
        logger.error("Internal server error: {}", ex.getMessage(), ex);
        String message = ex.getMessage();
        if (ex.getCause() != null) {
            message += " (Caused by: " + ex.getCause().getMessage() + ")";
        }
        ErrorResponseTo error = new ErrorResponseTo("50001", "Internal server error: " + message);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
    }
}

