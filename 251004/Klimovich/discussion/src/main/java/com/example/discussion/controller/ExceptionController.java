package com.example.discussion.controller;

import com.example.discussion.exception.ServiceException;
import jakarta.validation.ValidationException;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;


@RestControllerAdvice
public class ExceptionController {

    @ExceptionHandler(ServiceException.class)
    public ResponseEntity<ErrorResponse> handleServiceException(ServiceException ex) {
        return ResponseEntity.status(ex.getErrorCode())
                .contentType(MediaType.APPLICATION_JSON)
                .body(new ErrorResponse(ex.getErrorCode(), ex.getMessage(), ex.getMessage()));
    }
    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<ErrorResponse> handleValidationExeption(ValidationException ex) {
        return ResponseEntity.status(400)
                .contentType(MediaType.APPLICATION_JSON)
                .body(new ErrorResponse(400, ex.getMessage(), ex.getMessage()));
    }
    public record ErrorResponse(int code, String status, String message){}
}
