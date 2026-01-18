package org.discussion.dto.response;

public record NoteResponseToDto(
        String country,
        Long issueId,
        Long id,
        String content
) {
}