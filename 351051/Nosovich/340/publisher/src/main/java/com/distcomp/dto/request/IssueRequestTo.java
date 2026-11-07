package com.distcomp.dto.request;

import java.util.List;

/**
 * Incoming representation of an {@link com.distcomp.model.Issue}.
 *
 * <p>{@code tags} holds tag <em>names</em>: an existing tag with that name is
 * reused, a missing one is created together with the issue. Timestamps are
 * never accepted from a client.</p>
 */
public record IssueRequestTo(
        Long id,
        Long creatorId,
        String title,
        String content,
        List<String> tags) {
}
