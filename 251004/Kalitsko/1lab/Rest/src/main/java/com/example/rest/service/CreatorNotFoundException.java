package com.example.rest.service;

public class CreatorNotFoundException extends RuntimeException {
    public CreatorNotFoundException(Long id) {
        super("Creator with id " + id + " not found");
    }
}
