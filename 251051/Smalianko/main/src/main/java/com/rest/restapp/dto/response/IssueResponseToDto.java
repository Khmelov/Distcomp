package com.rest.restapp.dto.response;

public record IssueResponseToDto(
        Long id,
        Long authorId,
        String title,
        String content,
        String created,
        String modified
) {
}
