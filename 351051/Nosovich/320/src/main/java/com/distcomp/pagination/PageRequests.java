package com.distcomp.pagination;

import com.distcomp.exception.ValidationException;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.Set;

/**
 * Builds a {@link Pageable} out of the optional request parameters.
 *
 * <p>Every collection endpoint accepts {@code page}, {@code size} and
 * {@code sort} (for example {@code sort=login,desc}). Sorting properties are
 * validated against a per-endpoint whitelist so that a typo cannot reach the
 * persistence layer.</p>
 */
public final class PageRequests {

    /** Page size used when the client asks for a page but omits its size. */
    public static final int DEFAULT_SIZE = 20;

    private PageRequests() {
    }

    /**
     * @param page    zero-based page number, {@code null} for the whole result
     * @param size    page size, {@code null} for the whole result
     * @param sort    sort expression such as {@code login,desc}; may be {@code null}
     * @param defaultSort sort expression applied when {@code sort} is absent
     * @param allowed allowed sort properties
     * @return a pageable that is unpaged when neither page nor size is given
     */
    public static Pageable of(Integer page, Integer size, String sort, String defaultSort,
                              Set<String> allowed) {
        Sort order = parse(sort == null ? defaultSort : sort, allowed);
        if (page == null && size == null) {
            return Pageable.unpaged(order);
        }
        int pageNumber = page == null ? 0 : Math.max(page, 0);
        int pageSize = size == null ? DEFAULT_SIZE : Math.max(size, 1);
        return PageRequest.of(pageNumber, pageSize, order);
    }

    private static Sort parse(String expression, Set<String> allowed) {
        if (expression == null || expression.isBlank()) {
            return Sort.unsorted();
        }
        Sort sort = Sort.unsorted();
        for (String part : expression.split(";")) {
            String[] tokens = part.split(",");
            String property = tokens[0].trim();
            if (!allowed.contains(property)) {
                throw new ValidationException("sort",
                        "contains unsupported property '" + property + "', allowed: " + allowed);
            }
            Sort.Direction direction = tokens.length > 1 && "desc".equalsIgnoreCase(tokens[1].trim())
                    ? Sort.Direction.DESC
                    : Sort.Direction.ASC;
            sort = sort.and(Sort.by(direction, property));
        }
        return sort;
    }
}
