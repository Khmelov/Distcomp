package com.blog.discussion.exception;

import com.blog.discussion.dto.response.MessageResponseTo;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.LocalDateTime;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleResourceNotFoundException(ResourceNotFoundException ex) {
        ErrorResponse error = new ErrorResponse(
                HttpStatus.NOT_FOUND.value() * 100 + 1,
                ex.getMessage(),
                LocalDateTime.now()
        );
        return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleIllegalArgumentException(IllegalArgumentException ex) {
        ErrorResponse error = new ErrorResponse(
                HttpStatus.BAD_REQUEST.value() * 100 + 2,
                ex.getMessage(),
                LocalDateTime.now()
        );
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }
    @ExceptionHandler(Exception.class)
    public ResponseEntity<MessageResponseTo> handleAllExceptions(Exception ex) {
        System.err.println("Global exception handler caught: " + ex.getMessage());
        ex.printStackTrace();

        // Всегда возвращаем 404 для ненайденных сообщений
        // или создаем generic error response
        MessageResponseTo error = new MessageResponseTo();
        error.setId(null);
        error.setContent("Service unavailable");
        error.setState("ERROR");
        error.setApproved(false);
        error.setDeclined(false);
        error.setPending(false);

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }
}


