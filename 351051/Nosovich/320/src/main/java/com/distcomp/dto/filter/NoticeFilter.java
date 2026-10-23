package com.distcomp.dto.filter;

/**
 * Optional filtering criteria of the notice collection.
 *
 * @param issueId identifier of the related issue
 */
public record NoticeFilter(Long issueId) {

    /** @return a filter that accepts every notice */
    public static NoticeFilter empty() {
        return new NoticeFilter(null);
    }
}
