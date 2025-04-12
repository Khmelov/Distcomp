package com.lab1.lab1DC.exception;

public class ErrorResponse {
    private String errorCode;
    private String errorMessage;
    private int statusCode;

    public ErrorResponse(String errorCode, String errorMessage, int statusCode) {
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
        this.statusCode = statusCode;
    }

}
