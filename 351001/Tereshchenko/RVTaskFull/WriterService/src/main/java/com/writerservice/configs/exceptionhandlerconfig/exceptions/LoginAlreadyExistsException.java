package com.writerservice.configs.exceptionhandlerconfig.exceptions;

public class LoginAlreadyExistsException extends RuntimeException {
    public LoginAlreadyExistsException(String message) {
        super(message);
    }
}
