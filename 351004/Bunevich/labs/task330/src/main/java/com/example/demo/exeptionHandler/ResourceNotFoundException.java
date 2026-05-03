package com.example.demo.exeptionHandler;

public class ResourceNotFoundException extends RuntimeException {
    public ResourceNotFoundException(String resourceName, Long id) {
        super(resourceName + " with id " + id + " not found");
    }
}
