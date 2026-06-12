package com.bsuir.romanmuhtasarov.exceptions;

public record ErrorResponseTo(
        String errorComment,
        String errorCode) {
}
