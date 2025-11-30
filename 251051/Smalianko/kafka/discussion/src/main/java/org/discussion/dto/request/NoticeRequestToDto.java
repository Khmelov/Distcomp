package org.discussion.dto.request;


public record NoticeRequestToDto(
        Long issueId,
        String content
) {
}