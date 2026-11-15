package com.example.task330.publisher.exception;

// Исключение возникает, если запрашиваемый ресурс не найден.
public class ResourceNotFoundException extends RuntimeException {

    // Передача сообщения об ошибке в родительский класс.
    public ResourceNotFoundException(String message) {
        super(message);
    }
}
