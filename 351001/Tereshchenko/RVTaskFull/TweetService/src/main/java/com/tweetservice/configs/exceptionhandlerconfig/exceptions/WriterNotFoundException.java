package com.tweetservice.configs.exceptionhandlerconfig.exceptions;

public class WriterNotFoundException extends RuntimeException {
    public WriterNotFoundException(String message) {
        super(message);
    }
}
