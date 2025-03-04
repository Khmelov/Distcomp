package com.bsuir.dc.controller;

import com.bsuir.dc.dto.response.ErrorResponse;
import com.bsuir.dc.dto.response.ValidationFailedResponse;
import com.bsuir.dc.util.exception.CustomException;
import com.bsuir.dc.util.exception.EntityNotFoundException;
import com.bsuir.dc.util.exception.ValidationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleCustomException(CustomException e) {
        return new ResponseEntity<>(new ErrorResponse(e.getErrorCode(), e.getMessage()), e.getHttpStatus());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ValidationFailedResponse> handleValidationExceptions(MethodArgumentNotValidException e) {
        return createValidationFailedResponse(e.getBindingResult());
    }

    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<ValidationFailedResponse> handeValidationException(ValidationException e) {
        return createValidationFailedResponse(e.getBindingResult());
    }

    private ResponseEntity<ValidationFailedResponse> createValidationFailedResponse(BindingResult bindingResult){
        Map<String, String> errors = new HashMap<>();
        for (FieldError fieldError : bindingResult.getFieldErrors()) {
            errors.put(fieldError.getField(), fieldError.getDefaultMessage());
        }

        ValidationFailedResponse errorResponse = new ValidationFailedResponse(40301, errors);
        return new ResponseEntity<>(errorResponse, HttpStatus.FORBIDDEN);
    }
}