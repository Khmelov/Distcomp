package com.example.demo.exception;

public class DuplicateLoginException extends RuntimeException {
    public DuplicateLoginException(String message) {
        super(message);
    }
}