package com.markerservice.configs.exceptionhandlerconfig;

import com.markerservice.configs.exceptionhandlerconfig.exceptions.MarkerAlreadyExistsException;
import com.markerservice.configs.exceptionhandlerconfig.exceptions.MarkerNotFoundException;
import com.markerservice.configs.exceptionhandlerconfig.exceptions.TweetNotFoundException;
import jakarta.validation.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(exception = {MarkerNotFoundException.class, TweetNotFoundException.class})
    public ResponseEntity<Map<String, String>> handleNotFoundException(RuntimeException e) {
        return new ResponseEntity<>(Map.of("error", e.getMessage()), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(MarkerAlreadyExistsException.class)
    public ResponseEntity<Map<String, String>> handleAlreadyExistsException(MarkerAlreadyExistsException e) {
        return new ResponseEntity<>(Map.of("error", e.getMessage()), HttpStatus.CONFLICT);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<Map<String, String>> handleConstraintViolationException(RuntimeException e) {
        return new ResponseEntity<>(Map.of("error", e.getMessage()), HttpStatus.BAD_REQUEST);
    }

}
