package com.tweetservice.configs.exceptionhandlerconfig.exceptions;

public class TweetAlreadyExistsException extends RuntimeException {
    public TweetAlreadyExistsException(String message) {
        super(message);
    }
}
