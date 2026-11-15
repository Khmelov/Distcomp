package com.example.task330.publisher.exception;

// Исключение возникает при попытке создать редактора
// с логином, который уже используется другим редактором.
public class DuplicateLoginException extends RuntimeException {

    // Создание исключения с поясняющим сообщением.
    public DuplicateLoginException(String message) {
        super(message);
    }
}
