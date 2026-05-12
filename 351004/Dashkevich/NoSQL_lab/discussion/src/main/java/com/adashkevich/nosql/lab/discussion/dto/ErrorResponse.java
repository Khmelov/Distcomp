package com.adashkevich.nosql.lab.discussion.dto;

public class ErrorResponse {
    public String errorMessage;
    public String errorCode;

    public ErrorResponse(String errorMessage, String errorCode) {
        this.errorMessage = errorMessage;
        this.errorCode = errorCode;
    }
}
