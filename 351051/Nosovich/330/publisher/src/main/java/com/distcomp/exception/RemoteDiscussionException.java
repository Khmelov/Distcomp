package com.distcomp.exception;

import org.springframework.http.HttpStatus;

/**
 * Raised when the discussion module answers with an application error.
 */
public class RemoteDiscussionException extends ApiException {

    public RemoteDiscussionException(HttpStatus status, String errorCode, String message) {
        super(status, errorCode, message);
    }
}
