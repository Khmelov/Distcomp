package com.distcomp.dto.request;

/**
 * Incoming representation of a {@link com.distcomp.model.Notice}.
 */
public record NoticeRequestTo(
        Long id,
        Long issueId,
        String content) {
}
