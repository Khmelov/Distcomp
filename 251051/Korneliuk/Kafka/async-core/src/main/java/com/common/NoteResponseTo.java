package com.common;

public record NoteResponseTo(
        Long id,
        Long issueId,
        String content,
        NoteState state
) {
}
