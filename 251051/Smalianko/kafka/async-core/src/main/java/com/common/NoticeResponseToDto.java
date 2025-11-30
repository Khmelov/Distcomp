package com.common;

public record NoticeResponseToDto(
        Long id,
        Long issueId,
        String content,
        NoticeState state
) {
}
