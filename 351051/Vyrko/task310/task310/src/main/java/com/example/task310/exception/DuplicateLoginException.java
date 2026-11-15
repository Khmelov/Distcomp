package com.example.task310.exception;

// Исключение при попытке создать редактора с занятым логином.
public class DuplicateLoginException extends RuntimeException {

    // Передача сообщения об ошибке в родительский класс.
    public DuplicateLoginException(String message) {
        super(message);
    }
}