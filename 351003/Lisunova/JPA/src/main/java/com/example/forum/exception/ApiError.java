package com.example.forum.exception;

public class ApiError {
    private String errorMessage;
    private String errorCode; // формат: первые 3 цифры = HTTP статус, 2 последние — твоя детализация

    public ApiError() {
    }

    public ApiError(String errorMessage, String errorCode) {
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
