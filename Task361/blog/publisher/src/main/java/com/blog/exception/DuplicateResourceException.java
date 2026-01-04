package com.blog.exception;

public class DuplicateResourceException extends RuntimeException {

    public DuplicateResourceException(String message) {
        super(message);
    }

    public DuplicateResourceException(String resource, String field, String value) {
        super(resource + " with " + field + " '" + value + "' already exists");
    }
}