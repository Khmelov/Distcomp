package com.distcomp.errorhandling;

import com.distcomp.errorhandling.exceptions.BusinessValidationException;
import com.distcomp.errorhandling.model.ErrorResponse;
import com.distcomp.errorhandling.model.ValidationError;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import lombok.NonNull;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.bind.support.WebExchangeBindException;

import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

@RestControllerAdvice(basePackages = "com.distcomp.controller")
public class GlobalExceptionHandler {


    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ErrorResponse> handleConstraintViolation(final @NonNull ConstraintViolationException ex) {
        final List<ValidationError> errors = ex.getConstraintViolations().stream()
                .map((final ConstraintViolation<?> violation) -> ValidationError.builder()
                        .field(violation.getPropertyPath().toString())
                        .message(violation.getMessage())
                        .build())
                .collect(Collectors.toList());
        return buildResponse(HttpStatus.BAD_REQUEST, "Validation Error", errors);
    }

    @ExceptionHandler(WebExchangeBindException.class)
    public ResponseEntity<ErrorResponse> handleWebExchangeBind(final @NonNull WebExchangeBindException ex) {
        final List<ValidationError> errors = ex.getBindingResult().getFieldErrors().stream()
                .map((final FieldError error) -> ValidationError.builder()
                        .field(error.getField())
                        .message(error.getDefaultMessage())
                        .build())
                .collect(Collectors.toList());
        return buildResponse(HttpStatus.BAD_REQUEST, "Invalid CreateRequest", errors);
    }


    @ExceptionHandler(BusinessValidationException.class)
    public ResponseEntity<ErrorResponse> handleBusinessException(final @NonNull BusinessValidationException ex) {
        return buildResponse(HttpStatus.FORBIDDEN, "Business Validation Error", ex.getErrors());
    }

    private ResponseEntity<ErrorResponse> buildResponse(final @NonNull HttpStatus status,
                                                        final @NonNull String message,
                                                        final @NonNull List<ValidationError> details) {
        return ResponseEntity.status(status)
                .body(ErrorResponse.builder()
                        .status(status.value())
                        .message(message)
                        .timestamp(Instant.now())
                        .details(details)
                        .build()
                );
    }

}
