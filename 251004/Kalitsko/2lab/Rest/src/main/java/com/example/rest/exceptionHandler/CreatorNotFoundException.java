package com.example.rest.exceptionHandler;

public class CreatorNotFoundException extends RuntimeException {
    public CreatorNotFoundException(Long id) {
        super("Creator with id " + id + " not found");
    }
}
