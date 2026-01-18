package com.rest.restapp.exception;

public class DuplicateException extends RuntimeException {
    public DuplicateException(String login) {
        super("User with login '" + login + "' already exists");
    }
}