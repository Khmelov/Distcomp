package com.tweetservice.configs.exceptionhandlerconfig.exceptions;

public class TweetTitleAlreadyExistsException extends RuntimeException {
    public TweetTitleAlreadyExistsException(String message) {
        super(message);
    }
}
