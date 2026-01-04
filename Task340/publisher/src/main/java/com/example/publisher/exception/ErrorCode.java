package com.example.publisher.exception;

public enum ErrorCode {
    // 4xx
    VALIDATION_FAILED(40000),
    USER_LOGIN_EXISTS(40902),
    USER_NOT_FOUND(40401),
    STORY_NOT_FOUND(40402),
    LABEL_NOT_FOUND(40403),
    COMMENT_NOT_FOUND(40404),
    USER_ID_REQUIRED_FOR_UPDATE(40003),
    STORY_USER_NOT_FOUND(40405),

    // 5xx
    INTERNAL_ERROR(50001);

    public final int code;

    ErrorCode(int code) {
        this.code = code;
    }
}