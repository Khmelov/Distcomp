package com.distcomp.discussion.exception;

/**
 * Body returned for every failed request.
 *
 * @param errorMessage human readable explanation
 * @param errorCode    five-digit application code
 */
public record ErrorResponse(String errorMessage, String errorCode) {
}
