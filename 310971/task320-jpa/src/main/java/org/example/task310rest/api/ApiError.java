package org.example.task310rest.api;

public class ApiError {
    private String errorMessage;
    private String errorCode;

    public ApiError(String errorMessage, String errorCode) {
        this.errorMessage = errorMessage;
        this.errorCode = errorCode;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public String getErrorCode() {
        return errorCode;
    }
}


