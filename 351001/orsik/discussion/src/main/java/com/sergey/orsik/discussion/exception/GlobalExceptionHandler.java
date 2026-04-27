package com.sergey.orsik.discussion.exception;

import com.sergey.orsik.discussion.dto.ErrorResponseTo;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<ErrorResponseTo> handleEntityNotFound(
            EntityNotFoundException ex,
            HttpServletRequest request) {
        ErrorResponseTo body = ErrorResponseTo.builder()
                .timestamp(Instant.now())
                .errorCode(errorCode(HttpStatus.NOT_FOUND.value(), 1))
                .errorMessage(ex.getMessage())
                .path(request.getRequestURI())
                .build();
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(body);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponseTo> handleValidation(
            MethodArgumentNotValidException ex,
            HttpServletRequest request) {
        List<ErrorResponseTo.FieldErrorTo> errors = ex.getBindingResult().getAllErrors().stream()
                .filter(FieldError.class::isInstance)
                .map(FieldError.class::cast)
                .map(e -> new ErrorResponseTo.FieldErrorTo(
                        e.getField(),
                        errorCode(HttpStatus.BAD_REQUEST.value(), 10),
                        e.getDefaultMessage() != null ? e.getDefaultMessage() : "invalid"
                ))
                .collect(Collectors.toList());
        ErrorResponseTo body = ErrorResponseTo.builder()
                .timestamp(Instant.now())
                .errorCode(errorCode(HttpStatus.BAD_REQUEST.value(), 1))
                .errorMessage("Validation failed")
                .path(request.getRequestURI())
                .errors(errors)
                .build();
        return ResponseEntity.badRequest().body(body);
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ErrorResponseTo> handleTypeMismatch(
            MethodArgumentTypeMismatchException ex,
            HttpServletRequest request) {
        String message = ex.getName() != null && "id".equals(ex.getName())
                ? "Invalid id: must be a number"
                : "Invalid parameter type";
        ErrorResponseTo body = ErrorResponseTo.builder()
                .timestamp(Instant.now())
                .errorCode(errorCode(HttpStatus.BAD_REQUEST.value(), 2))
                .errorMessage(message)
                .path(request.getRequestURI())
                .build();
        return ResponseEntity.badRequest().body(body);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponseTo> handleMessageNotReadable(
            HttpMessageNotReadableException ex,
            HttpServletRequest request) {
        ErrorResponseTo body = ErrorResponseTo.builder()
                .timestamp(Instant.now())
                .errorCode(errorCode(HttpStatus.BAD_REQUEST.value(), 3))
                .errorMessage("Invalid request body")
                .path(request.getRequestURI())
                .build();
        return ResponseEntity.badRequest().body(body);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponseTo> handleIllegalArgument(
            IllegalArgumentException ex,
            HttpServletRequest request) {
        ErrorResponseTo body = ErrorResponseTo.builder()
                .timestamp(Instant.now())
                .errorCode(errorCode(HttpStatus.BAD_REQUEST.value(), 4))
                .errorMessage(ex.getMessage())
                .path(request.getRequestURI())
                .build();
        return ResponseEntity.badRequest().body(body);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponseTo> handleUnexpected(
            Exception ex,
            HttpServletRequest request) {
        ErrorResponseTo body = ErrorResponseTo.builder()
                .timestamp(Instant.now())
                .errorCode(errorCode(HttpStatus.INTERNAL_SERVER_ERROR.value(), 1))
                .errorMessage("Internal server error")
                .path(request.getRequestURI())
                .build();
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(body);
    }

    private String errorCode(int status, int detailsCode) {
        return String.format("%03d%02d", status, detailsCode);
    }
}
