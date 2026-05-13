package com.adashkevich.rest.lab.dto.error;

public class ErrorResponse {
    public String errorMessage;
    public String errorCode;

    public ErrorResponse() {}

    public ErrorResponse(String errorMessage, String errorCode) {
        this.errorMessage = errorMessage;
        this.errorCode = errorCode;
    }
}
