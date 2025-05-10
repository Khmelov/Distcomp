package com.example.discussion.exception;


public class ServiceException extends RuntimeException{
    private final int errorCode;

    public ServiceException(String devMessage, int errorCode) {
        super(devMessage);
        this.errorCode = errorCode;
    }

    public int getErrorCode() {
        return errorCode;
    }
}
