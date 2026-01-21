package com.example.app.exception;

public record ApiError(
        String errorMessage,
        int errorCode
) {}