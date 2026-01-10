package com.blog.exception;

import com.blog.dto.ErrorResponseTo;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.stream.Collectors;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<ErrorResponseTo> handleEntityNotFoundException(EntityNotFoundException ex) {
        ErrorResponseTo error = new ErrorResponseTo(ex.getMessage(), "40401");
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponseTo> handleIllegalArgumentException(IllegalArgumentException ex) {
        ErrorResponseTo error = new ErrorResponseTo(ex.getMessage(), "40304");
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(error);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponseTo> handleValidationException(MethodArgumentNotValidException ex) {
        String errorMessage = ex.getBindingResult().getFieldErrors().stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .collect(Collectors.joining(", "));
        ErrorResponseTo error = new ErrorResponseTo(errorMessage, "40002");
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ErrorResponseTo> handleDataIntegrityViolationException(DataIntegrityViolationException ex) {
        String message = ex.getMessage();
        if (message.contains("duplicate key value violates unique constraint")) {
            if (message.contains("login")) {
                ErrorResponseTo error = new ErrorResponseTo("Writer with this login already exists", "40301");
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(error);
            }
        } else if (message.contains("violates foreign key constraint")) {
            ErrorResponseTo error = new ErrorResponseTo("Cannot delete entity with existing references", "40302");
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(error);
        }
        ErrorResponseTo error = new ErrorResponseTo("Data integrity violation", "40303");
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(error);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponseTo> handleGenericException(Exception ex) {
        ErrorResponseTo error = new ErrorResponseTo("Internal server error", "50001");
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
    }
}