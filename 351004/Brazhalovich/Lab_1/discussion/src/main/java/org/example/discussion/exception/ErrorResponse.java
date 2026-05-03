package org.example.discussion.exception;

import lombok.Data;

@Data
public class ErrorResponse {
    private String errorMessage;
    private int errorCode;

    public ErrorResponse(String errorMessage, int errorCode) {
        this.errorMessage = errorMessage;
        this.errorCode = errorCode;
    }
}