package com.tweetservice.configs.exceptionhandlerconfig.exceptions;

public class TweetNotFoundException extends RuntimeException {
    public TweetNotFoundException(String message) {
        super(message);
    }
}
