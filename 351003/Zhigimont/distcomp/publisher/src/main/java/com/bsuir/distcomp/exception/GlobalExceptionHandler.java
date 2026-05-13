package com.bsuir.distcomp.exception;


import lombok.NonNull;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.LinkedHashMap;
import java.util.Map;

@ControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<?> handleBadCredentials(BadCredentialsException e) {
        Map<String, Object> error = new LinkedHashMap<>();
        error.put("errorMessage", e.getMessage());
        error.put("errorCode", "40102");
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<?> handleRuntime(RuntimeException e) {
        Map<String, Object> error = new LinkedHashMap<>();
        error.put("errorMessage", e.getMessage());
        error.put("errorCode", "40000");
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    @ExceptionHandler(EntityAlreadyExistsException.class)
    public ResponseEntity<@NonNull ErrorResponse> handleAlreadyExists(EntityAlreadyExistsException ex) {
        ErrorResponse error = new ErrorResponse(ex.getMessage(), 403);
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(error);
    }

}

