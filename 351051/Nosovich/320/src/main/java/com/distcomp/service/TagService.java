package com.distcomp.service;

import com.distcomp.dto.filter.TagFilter;
import com.distcomp.dto.request.TagRequestTo;
import com.distcomp.dto.response.TagResponseTo;
import com.distcomp.pagination.PagedResult;
import org.springframework.data.domain.Pageable;

import java.util.List;

/**
 * Business operations over {@link com.distcomp.model.Tag} entities.
 */
public interface TagService {

    TagResponseTo create(TagRequestTo request);

    TagResponseTo findById(Long id);

    /**
     * @param filter   optional filtering criteria
     * @param pageable paging and sorting instructions
     * @return the matching tags and the total number of matches
     */
    PagedResult<TagResponseTo> findAll(TagFilter filter, Pageable pageable);

    TagResponseTo update(Long id, TagRequestTo request);

    /**
     * Removes a tag and detaches it from every issue that used it.
     */
    void delete(Long id);

    List<TagResponseTo> findByIssueId(Long issueId);
}
