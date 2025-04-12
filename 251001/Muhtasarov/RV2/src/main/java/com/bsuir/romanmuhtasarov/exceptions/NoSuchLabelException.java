package com.bsuir.romanmuhtasarov.exceptions;

public class NoSuchLabelException extends IllegalArgumentException{
    private final Long tagId;

    public NoSuchLabelException(Long tagId) {
        super(String.format("Tag with id %d is not found in DB", tagId));
        this.tagId = tagId;
    }
}
