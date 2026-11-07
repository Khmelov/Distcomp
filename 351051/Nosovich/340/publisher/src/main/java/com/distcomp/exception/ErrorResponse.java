package com.distcomp.exception;

/**
 * Body returned for every failed request.
 *
 * @param errorMessage human readable explanation
 * @param errorCode    five-digit application code, first three digits repeat the HTTP status
 */
public record ErrorResponse(String errorMessage, String errorCode) {
}
