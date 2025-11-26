package org.discussion.dto.response;

public record NoticeResponseToDto(
        String country,
        Long issueId,
        Long id,
        String content
) {
}