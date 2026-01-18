package org.discussion.dto.request;


public record NoteRequestToDto(
        Long issueId,
        String content
) {
}