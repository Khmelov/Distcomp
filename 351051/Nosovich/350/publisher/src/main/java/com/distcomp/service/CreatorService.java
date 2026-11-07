package com.distcomp.service;

import com.distcomp.dto.filter.CreatorFilter;
import com.distcomp.dto.request.CreatorRequestTo;
import com.distcomp.dto.response.CreatorResponseTo;
import com.distcomp.pagination.PagedResult;
import org.springframework.data.domain.Pageable;

/**
 * Business operations over {@link com.distcomp.model.Creator} entities.
 */
public interface CreatorService {

    CreatorResponseTo create(CreatorRequestTo request);

    CreatorResponseTo findById(Long id);

    /**
     * @param filter   optional filtering criteria
     * @param pageable paging and sorting instructions
     * @return the matching creators and the total number of matches
     */
    PagedResult<CreatorResponseTo> findAll(CreatorFilter filter, Pageable pageable);

    CreatorResponseTo update(Long id, CreatorRequestTo request);

    /**
     * Removes a creator together with the issues and notices that depend on it.
     */
    void delete(Long id);

    /**
     * @param issueId identifier of the issue
     * @return the creator who wrote the issue
     */
    CreatorResponseTo findByIssueId(Long issueId);
}
