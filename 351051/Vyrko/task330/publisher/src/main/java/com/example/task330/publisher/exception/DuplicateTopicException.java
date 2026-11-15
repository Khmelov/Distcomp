package com.example.task330.publisher.exception;

// Исключение возникает при попытке создать тему с существующим заголовком.
public class DuplicateTopicException extends RuntimeException {

    public DuplicateTopicException(String message) {
        super(message);
    }
}
