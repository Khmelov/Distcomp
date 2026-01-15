package com.example.task310.error;

public class BadRequestException extends RuntimeException {
    public BadRequestException(String msg) { super(msg); }
}
