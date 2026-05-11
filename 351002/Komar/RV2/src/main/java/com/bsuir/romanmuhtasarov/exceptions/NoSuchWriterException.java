package com.bsuir.romanmuhtasarov.exceptions;

public class NoSuchWriterException extends IllegalArgumentException {
    private final Long writerId;

    public NoSuchWriterException(Long writerId) {
        super(String.format("Writer with id %d is not found in DB", writerId));
        this.writerId = writerId;
    }
}
