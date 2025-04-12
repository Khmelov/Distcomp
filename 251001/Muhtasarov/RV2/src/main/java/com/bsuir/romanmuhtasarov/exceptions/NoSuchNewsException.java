package com.bsuir.romanmuhtasarov.exceptions;

public class NoSuchNewsException extends IllegalArgumentException {
    private final Long tweetId;

    public NoSuchNewsException(Long tweetId) {
        super(String.format("Tweet with id %d is not found in DB", tweetId));
        this.tweetId = tweetId;
    }
}
