package com.example.demo.error;

public class TitleAlreadyExistsException extends RuntimeException {
        public TitleAlreadyExistsException(String message) {
            super(message);
        }
}

