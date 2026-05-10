package com.tweetservice.configs.exceptionhandlerconfig;

import com.tweetservice.configs.exceptionhandlerconfig.exceptions.TweetAlreadyExistsException;
import com.tweetservice.configs.exceptionhandlerconfig.exceptions.TweetNotFoundException;
import com.tweetservice.configs.exceptionhandlerconfig.exceptions.TweetTitleAlreadyExistsException;
import com.tweetservice.configs.exceptionhandlerconfig.exceptions.WriterNotFoundException;
import jakarta.validation.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(TweetNotFoundException.class)
    public ResponseEntity<Map<String, String>> handleNotFoundException(RuntimeException e) {
        return new ResponseEntity<>(Map.of("error", e.getMessage()), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(TweetAlreadyExistsException.class)
    public ResponseEntity<Map<String, String>> handleAlreadyExistsException(RuntimeException e) {
        return new ResponseEntity<>(Map.of("error", e.getMessage()), HttpStatus.CONFLICT);
    }

    @ExceptionHandler(TweetTitleAlreadyExistsException.class)
    public ResponseEntity<Map<String, String>> handleTitleAlreadyExistsException(RuntimeException e) {
        return new ResponseEntity<>(Map.of("error", e.getMessage()), HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<Map<String, String>> handleConstraintViolationException(RuntimeException e) {
        return new ResponseEntity<>(Map.of("error", e.getMessage()), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(WriterNotFoundException.class)
    public ResponseEntity<Map<String, String>> handleWriterNotFoundException(RuntimeException e) {
        return new ResponseEntity<>(Map.of("error", e.getMessage()), HttpStatus.BAD_REQUEST);
    }

}
