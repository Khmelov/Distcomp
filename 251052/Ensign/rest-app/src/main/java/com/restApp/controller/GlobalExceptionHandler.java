package com.restApp.controller;

import com.restApp.dto.ErrorResponse;
import com.restApp.exception.BusinessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

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
}
