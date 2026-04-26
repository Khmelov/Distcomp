package com.example.common.dto;

public record ErrorResponse(
        String errorMessage,
        String errorCode
) {}