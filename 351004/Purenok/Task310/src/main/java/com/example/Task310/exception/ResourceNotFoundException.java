package com.example.Task310.exception; // Убедитесь, что здесь exception!

public class ResourceNotFoundException extends RuntimeException {
    public ResourceNotFoundException(String message) {
        super(message);
    }
}