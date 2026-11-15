
package com.example.task320.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

// Общий обработчик исключений REST-контроллеров.
@RestControllerAdvice
public class GlobalExceptionHandler {

    // Обработка ситуации, когда ресурс не найден.
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleNotFound(
            ResourceNotFoundException exception) {

        return buildResponse(
                HttpStatus.NOT_FOUND,
                exception.getMessage(),
                "40401"
        );
    }

    // Обработка повторяющегося логина редактора.
    @ExceptionHandler(DuplicateLoginException.class)
    public ResponseEntity<ErrorResponse> handleDuplicateLogin(
            DuplicateLoginException exception) {

        return buildResponse(
                HttpStatus.FORBIDDEN,
                exception.getMessage(),
                "40301"
        );
    }

    // Обработка повторяющегося заголовка темы.
    @ExceptionHandler(DuplicateTopicException.class)
    public ResponseEntity<ErrorResponse> handleDuplicateTopic(
            DuplicateTopicException exception) {

        return buildResponse(
                HttpStatus.FORBIDDEN,
                exception.getMessage(),
                "40302"
        );
    }

    // Обработка ошибок проверки входных данных.
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidation(
            MethodArgumentNotValidException exception) {

        String message = exception.getBindingResult()
                .getFieldErrors()
                .stream()
                .findFirst()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .orElse("Invalid request data");

        return buildResponse(
                HttpStatus.BAD_REQUEST,
                message,
                "40001"
        );
    }

    // Формирование единого ответа об ошибке.
    private ResponseEntity<ErrorResponse> buildResponse(
            HttpStatus status,
            String message,
            String errorCode) {

        ErrorResponse response = new ErrorResponse(message, errorCode);

        return ResponseEntity.status(status).body(response);
    }
}
