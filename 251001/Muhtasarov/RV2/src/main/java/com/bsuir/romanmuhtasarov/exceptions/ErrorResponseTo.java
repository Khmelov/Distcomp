package com.bsuir.romanmuhtasarov.exceptions;

public record ErrorResponseTo(
        String errorMessage,
        String errorCode) {
}
