package com.example.demo.exeptionHandler;

public class ConflictException extends RuntimeException {
    public ConflictException(String message) {
        super(message);
    }
}
