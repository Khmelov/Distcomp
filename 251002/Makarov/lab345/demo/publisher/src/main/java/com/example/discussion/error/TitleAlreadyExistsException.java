package com.example.discussion.error;

public class TitleAlreadyExistsException extends RuntimeException {
        public TitleAlreadyExistsException(String message) {
            super(message);
        }
}

