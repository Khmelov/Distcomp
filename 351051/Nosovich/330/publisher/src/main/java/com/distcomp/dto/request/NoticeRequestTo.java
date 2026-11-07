package com.distcomp.dto.request;

import com.distcomp.common.notice.NoticeState;

/**
 * Incoming representation of a notice.
 *
 * <p>{@code state} is accepted for compatibility with clients that send a full
 * notice back, but the value is decided by the discussion module after
 * moderation.</p>
 */
public record NoticeRequestTo(
        Long id,
        Long issueId,
        String content,
        NoticeState state) {
}
