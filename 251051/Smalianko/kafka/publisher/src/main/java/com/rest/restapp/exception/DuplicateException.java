package com.rest.restapp.exception;

public class DuplicateException extends RuntimeException {
    public DuplicateException(String login) {
        super("Author with login '" + login + "' already exists");
    }
}