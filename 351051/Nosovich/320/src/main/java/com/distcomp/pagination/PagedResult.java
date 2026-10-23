package com.distcomp.pagination;

import org.springframework.data.domain.Page;

import java.util.List;

/**
 * One page of a collection together with the total number of matching rows.
 *
 * @param content items of the page
 * @param total   total number of rows matching the query
 * @param <T>     item type
 */
public record PagedResult<T>(List<T> content, long total) {

    /** Wraps a Spring Data page into the transport independent result. */
    public static <T> PagedResult<T> of(Page<T> page) {
        return new PagedResult<>(page.getContent(), page.getTotalElements());
    }
}
