package com.distcomp.dto.response;

/**
 * Outgoing representation of a {@link com.distcomp.model.Notice}.
 */
public record NoticeResponseTo(
        Long id,
        Long issueId,
        String content) {
}
