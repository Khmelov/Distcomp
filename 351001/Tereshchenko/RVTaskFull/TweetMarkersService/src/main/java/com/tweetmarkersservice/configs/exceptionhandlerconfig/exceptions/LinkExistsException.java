package com.tweetmarkersservice.configs.exceptionhandlerconfig.exceptions;

public class LinkExistsException extends RuntimeException {
    public LinkExistsException(String message) {
        super(message);
    }
}
