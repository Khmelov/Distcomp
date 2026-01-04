package com.example.discussion.exception;

public record ApiError(String errorMessage, int errorCode) {}