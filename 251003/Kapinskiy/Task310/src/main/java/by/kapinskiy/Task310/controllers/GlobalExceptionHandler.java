package by.kapinskiy.Task310.controllers;

import by.kapinskiy.Task310.DTOs.Responses.ErrorResponse;
import by.kapinskiy.Task310.DTOs.Responses.ValidationFailedResponse;
import by.kapinskiy.Task310.utils.exceptions.CustomInformativeException;
import by.kapinskiy.Task310.utils.exceptions.NotFoundException;
import by.kapinskiy.Task310.utils.exceptions.ValidationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<ErrorResponse> handleCustomException(CustomInformativeException ex) {
        return new ResponseEntity<>(new ErrorResponse(ex.getErrorCode(), ex.getMessage()), ex.getHttpStatus());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ValidationFailedResponse> handleValidationExceptions(MethodArgumentNotValidException ex) {
        return createValidationFailedResponse(ex.getBindingResult());
    }

    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<ValidationFailedResponse> handeValidationException(ValidationException ex) {
        return createValidationFailedResponse(ex.getBindingResult());
    }

    private ResponseEntity<ValidationFailedResponse> createValidationFailedResponse(BindingResult bindingResult){
        Map<String, String> errors = new HashMap<>();
        for (FieldError fieldError : bindingResult.getFieldErrors()) {
            errors.put(fieldError.getField(), fieldError.getDefaultMessage());
        }

        ValidationFailedResponse errorResponse = new ValidationFailedResponse(40001, errors);
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }
}
