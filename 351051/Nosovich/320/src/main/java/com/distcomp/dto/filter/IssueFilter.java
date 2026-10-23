package com.distcomp.dto.filter;

/**
 * Optional filtering criteria of the issue collection. All criteria are
 * combined with {@code AND} and may be used together.
 *
 * @param creatorId      identifier of the author
 * @param titleContains  fragment of the title, matched case-insensitively
 * @param tagName        exact name of a tag the issue must carry
 */
public record IssueFilter(Long creatorId, String titleContains, String tagName) {

    /** @return a filter that accepts every issue */
    public static IssueFilter empty() {
        return new IssueFilter(null, null, null);
    }
}
