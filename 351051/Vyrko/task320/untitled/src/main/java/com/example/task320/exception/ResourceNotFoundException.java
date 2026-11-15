package com.example.task320.exception;

// Исключение возникает, если запрашиваемый ресурс не найден.
public class ResourceNotFoundException extends RuntimeException {

    // Передача сообщения об ошибке в родительский класс.
    public ResourceNotFoundException(String message) {
        super(message);
    }
}