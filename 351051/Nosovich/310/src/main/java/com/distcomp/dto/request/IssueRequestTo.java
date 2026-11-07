package com.distcomp.dto.request;

import java.util.List;

/**
 * Incoming representation of an {@link com.distcomp.model.Issue}.
 *
 * <p>Timestamps are never accepted from a client: they are produced by the
 * service layer. {@code tagIds} is optional and only links existing tags.</p>
 */
public record IssueRequestTo(
        Long id,
        Long creatorId,
        String title,
        String content,
        List<Long> tagIds) {
}
