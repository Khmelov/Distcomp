package org.discussion.dto.request;


public record NoteRequestTo(
        Long issueId,
        String content
) {
}