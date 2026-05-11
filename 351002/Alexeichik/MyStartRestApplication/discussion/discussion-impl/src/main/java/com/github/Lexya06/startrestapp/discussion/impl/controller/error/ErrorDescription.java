package com.github.Lexya06.startrestapp.discussion.impl.controller.error;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@JsonFormat(shape =  JsonFormat.Shape.OBJECT)
public enum ErrorDescription {
    CONSTRAINT_VIOLATION(40301, HttpStatus.FORBIDDEN),
    ENTITY_NOT_FOUND(40401, HttpStatus.NOT_FOUND),
    ENTITIES_NOT_FOUND(40402, HttpStatus.NOT_FOUND),
    NO_RESOURCE_FOUND(40403, HttpStatus.NOT_FOUND),
    MESSAGE_NOT_READABLE(40001, HttpStatus.BAD_REQUEST),
    BAD_REQUEST_BODY(40003, HttpStatus.BAD_REQUEST),
    REQUEST_METHOD_NOT_SUPPORTED(40501, HttpStatus.METHOD_NOT_ALLOWED),
    TYPE_MISMATCH(40002, HttpStatus.BAD_REQUEST),
    INTERNAL_SERVER_ERROR(50001, HttpStatus.INTERNAL_SERVER_ERROR);



    @Getter
    final int errorCode;

    @Getter
    final HttpStatus status;



    ErrorDescription(int errorCode, HttpStatus status) {
        this.errorCode = errorCode;
        this.status = status;
    }



}
