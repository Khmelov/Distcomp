package com.bsuir.distcomp.exception;


import lombok.NonNull;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.LinkedHashMap;
import java.util.Map;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<@NonNull ErrorResponse> handleNotFound(EntityNotFoundException ex) {
        ErrorResponse error = new ErrorResponse(ex.getMessage(), 404);
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }

    @ExceptionHandler(EntityAlreadyExistsException.class)
    public ResponseEntity<@NonNull ErrorResponse> handleAlreadyExists(EntityAlreadyExistsException ex) {
        ErrorResponse error = new ErrorResponse(ex.getMessage(), 403);
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(error);
    }

}

