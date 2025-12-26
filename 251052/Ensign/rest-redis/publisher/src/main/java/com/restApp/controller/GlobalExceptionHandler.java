package com.restApp.controller;

import com.restApp.dto.ErrorResponse;
import com.restApp.exception.BusinessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ErrorResponse> handleBusinessException(BusinessException ex) {
        String errorCode = ex.getErrorCode();
        int statusCode = 500;
        try {
            if (errorCode != null && errorCode.length() >= 3) {
                statusCode = Integer.parseInt(errorCode.substring(0, 3));
            }
        } catch (NumberFormatException e) {
            // keep 500
        }

        return new ResponseEntity<>(
                new ErrorResponse(ex.getMessage(), errorCode),
                HttpStatus.valueOf(statusCode));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationException(MethodArgumentNotValidException ex) {
        String errorMessage = ex.getBindingResult().getFieldErrors().stream()
                .map(error -> error.getField() + " " + error.getDefaultMessage())
                .collect(Collectors.joining(", "));
        return new ResponseEntity<>(
                new ErrorResponse(errorMessage, "40000"),
                HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGenericException(Exception ex) {
        return new ResponseEntity<>(
                new ErrorResponse("Internal Server Error: " + ex.getMessage(), "50000"),
                HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
