package com.example.discussion.error;
public class DuplicateLoginException extends RuntimeException {
    public DuplicateLoginException(String message) {
        super(message);
    }
}