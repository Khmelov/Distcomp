package com.example.storyapp.exception;

public record ApiError(
        String errorMessage,
        int errorCode
) {}