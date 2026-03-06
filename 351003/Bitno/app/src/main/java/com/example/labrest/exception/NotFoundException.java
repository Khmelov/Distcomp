package com.example.labrest.exception;

public class NotFoundException extends RuntimeException {
    public NotFoundException(String type, Long id) { super(type + " with id " + id + " not found"); }
}