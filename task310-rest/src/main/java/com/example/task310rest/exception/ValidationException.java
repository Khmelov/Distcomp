package com.example.task310rest.exception;

/**
 * Исключение для ошибок валидации данных
 * Приводит к HTTP статусу 400 Bad Request
 */
public class ValidationException extends RuntimeException {
    
    public ValidationException(String message) {
        super(message);
    }
}
