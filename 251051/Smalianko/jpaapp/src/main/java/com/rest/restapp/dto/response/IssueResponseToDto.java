package com.rest.restapp.dto.response;

import java.util.List;

public record IssueResponseToDto(
        Long id,
        Long authorId,
        String title,
        String content,
        String created,
        String modified,
        List<String> tags
) {
}
