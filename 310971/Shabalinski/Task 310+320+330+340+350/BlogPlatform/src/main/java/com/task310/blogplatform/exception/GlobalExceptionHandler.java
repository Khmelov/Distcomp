package com.task310.blogplatform.exception;

import com.task310.blogplatform.dto.ErrorResponseTo;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

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

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponseTo> handleGenericException(Exception ex) {
        ErrorResponseTo error = new ErrorResponseTo("Internal server error: " + ex.getMessage(), "50001");
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
    }
}

