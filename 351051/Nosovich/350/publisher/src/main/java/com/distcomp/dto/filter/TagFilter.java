package com.distcomp.dto.filter;

/**
 * Optional filtering criteria of the tag collection.
 *
 * @param nameContains fragment of the name, matched case-insensitively
 */
public record TagFilter(String nameContains) {

    /** @return a filter that accepts every tag */
    public static TagFilter empty() {
        return new TagFilter(null);
    }
}
