package com.distcomp.dto.response;

import com.distcomp.common.notice.NoticeState;

/**
 * Outgoing representation of a notice.
 *
 * <p>The field set is the same as before the modularisation plus the mandatory
 * {@code state} introduced by the Kafka task.</p>
 */
public record NoticeResponseTo(
        Long id,
        Long issueId,
        String content,
        NoticeState state) {
}
