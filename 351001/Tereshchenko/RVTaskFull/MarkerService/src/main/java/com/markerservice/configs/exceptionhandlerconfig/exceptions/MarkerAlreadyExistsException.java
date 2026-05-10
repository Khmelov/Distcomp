package com.markerservice.configs.exceptionhandlerconfig.exceptions;

public class MarkerAlreadyExistsException extends RuntimeException {
    public MarkerAlreadyExistsException(String message) {
        super(message);
    }
}
