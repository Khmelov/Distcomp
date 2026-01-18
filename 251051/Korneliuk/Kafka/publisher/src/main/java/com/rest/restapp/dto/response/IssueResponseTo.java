package com.rest.restapp.dto.response;

import java.util.List;

public record IssueResponseTo(
        Long id,
        Long userId,
        String title,
        String content,
        String created,
        String modified,
        List<String> tags
) {
}
