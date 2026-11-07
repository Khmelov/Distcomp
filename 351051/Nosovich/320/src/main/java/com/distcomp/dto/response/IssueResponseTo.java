package com.distcomp.dto.response;

import java.time.Instant;
import java.util.List;

/**
 * Outgoing representation of an {@link com.distcomp.model.Issue}.
 *
 * <p>Timestamps are rendered as ISO-8601 strings. The related tags are
 * exposed as a collection of {@link TagResponseTo}.</p>
 */
public record IssueResponseTo(
        Long id,
        Long creatorId,
        String title,
        String content,
        Instant created,
        Instant modified,
        List<TagResponseTo> tags) {
}
