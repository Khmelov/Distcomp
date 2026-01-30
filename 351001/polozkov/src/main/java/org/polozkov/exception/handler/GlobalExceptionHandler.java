package org.polozkov.exception.handler;

import lombok.extern.slf4j.Slf4j;
import org.polozkov.dto.error.ErrorResponseDtoOut;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponseDtoOut> handleValidationExceptions(MethodArgumentNotValidException ex) {
        log.error("Validation exception: {}", ex.getMessage());

        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });

        String errorMessage = "Validation failed: " + String.join(", ", errors.values());

        ErrorResponseDtoOut errorResponseDtoOut = new ErrorResponseDtoOut(
                HttpStatus.BAD_REQUEST.value(),
                "400 BAD_REQUEST",
                errorMessage,
                LocalDateTime.now()
        );
        return new ResponseEntity<>(errorResponseDtoOut, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<ErrorResponseDtoOut> handleResponseStatusException(ResponseStatusException ex) {
        log.error("ResponseStatusException: {}", ex.getReason(), ex);

        ErrorResponseDtoOut errorResponseDtoOut = new ErrorResponseDtoOut(
                ex.getStatusCode().value(),
                ex.getStatusCode().toString(),
                ex.getReason(),
                LocalDateTime.now()
        );
        return new ResponseEntity<>(errorResponseDtoOut, ex.getStatusCode());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponseDtoOut> handleAllExceptions(Exception ex) {
        log.error("Unhandled exception: ", ex);

        ErrorResponseDtoOut errorResponseDtoOut = new ErrorResponseDtoOut(
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                "Internal server error",
                HttpStatus.INTERNAL_SERVER_ERROR.toString(),
                LocalDateTime.now()
        );
        return new ResponseEntity<>(errorResponseDtoOut, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
