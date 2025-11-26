package com.rest.restapp.dto.response;

public record NoticeResponseToDto(
        Long id,
        Long issueId,
        String content
) {
}
