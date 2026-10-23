package com.distcomp.dto.response;

import java.time.Instant;
import java.util.List;

/**
 * Outgoing representation of an {@link com.distcomp.model.Issue}.
 *
 * <p>Timestamps are rendered as ISO-8601 strings (for example
 * {@code 2025-01-31T10:15:30Z}).</p>
 */
public record IssueResponseTo(
        Long id,
        Long creatorId,
        String title,
        String content,
        Instant created,
        Instant modified,
        List<Long> tagIds) {
}
