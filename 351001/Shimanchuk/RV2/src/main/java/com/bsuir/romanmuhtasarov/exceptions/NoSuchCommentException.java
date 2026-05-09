package com.bsuir.romanmuhtasarov.exceptions;

public class NoSuchCommentException extends IllegalArgumentException {
    private final Long commentId;

    public NoSuchCommentException(Long commentId) {
        super(String.format("Writer with id %d is not found in DB", commentId));
        this.commentId = commentId;
    }
}
