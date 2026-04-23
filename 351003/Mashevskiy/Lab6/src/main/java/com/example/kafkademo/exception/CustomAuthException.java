package com.example.kafkademo.exception;

import lombok.Getter;

@Getter
public class CustomAuthException extends RuntimeException {

    private final String errorCode;

    public CustomAuthException(String message) {
        super(message);
        this.errorCode = "40100";
    }

    public CustomAuthException(String message, String errorCode) {
        super(message);
        this.errorCode = errorCode;
    }

    public CustomAuthException(String message, Throwable cause) {
        super(message, cause);
        this.errorCode = "40100";
    }

    public CustomAuthException(String message, String errorCode, Throwable cause) {
        super(message, cause);
        this.errorCode = errorCode;
    }
}