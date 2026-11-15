package com.example.task310.exception;

// Исключение, если запрашиваемый ресурс не найден.
public class ResourceNotFoundException extends RuntimeException {

    // Передача сообщения об ошибке в родительский класс.
    public ResourceNotFoundException(String message) {
        super(message);
    }
}