package com.markerservice.configs.exceptionhandlerconfig.exceptions;

public class MarkerNotFoundException extends RuntimeException {
    public MarkerNotFoundException(String message) {
        super(message);
    }
}
