package com.example.restApi.exception;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ErrorInfo {
    @JsonProperty("errorMessage")
    private String errorMessage;

    @JsonProperty("errorCode")
    private Integer errorCode;

    public ErrorInfo(String errorMessage, Integer errorCode) {
        this.errorMessage = errorMessage;
        this.errorCode = errorCode;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public Integer getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(Integer errorCode) {
        this.errorCode = errorCode;
    }
}
