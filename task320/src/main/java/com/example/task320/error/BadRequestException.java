package com.example.task320.error;

public class BadRequestException extends RuntimeException {
    public BadRequestException(String msg) { super(msg); }
}
