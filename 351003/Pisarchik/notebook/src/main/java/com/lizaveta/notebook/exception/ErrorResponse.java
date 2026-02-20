package com.lizaveta.notebook.exception;

/**
 * Standard error response DTO.
 */
public record ErrorResponse(String errorMessage, int errorCode) {
}
