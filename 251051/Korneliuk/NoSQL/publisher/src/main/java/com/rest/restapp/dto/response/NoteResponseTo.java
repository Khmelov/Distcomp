package com.rest.restapp.dto.response;

public record NoteResponseTo(
        Long id,
        Long issueId,
        String content
) {
}
