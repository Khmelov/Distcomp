
package com.example.task320.exception;

// DTO для передачи информации об ошибке клиенту.
public class ErrorResponse {

    private String errorMessage;
    private String errorCode;

    public ErrorResponse() {
    }

    public ErrorResponse(String errorMessage, String errorCode) {
        this.errorMessage = errorMessage;
        this.errorCode = errorCode;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public String getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(String errorCode) {
        this.errorCode = errorCode;
    }
}
