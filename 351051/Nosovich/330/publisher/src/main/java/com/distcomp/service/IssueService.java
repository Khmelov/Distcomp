package com.distcomp.service;

import com.distcomp.dto.filter.IssueFilter;
import com.distcomp.dto.request.IssueRequestTo;
import com.distcomp.dto.response.IssueResponseTo;
import com.distcomp.pagination.PagedResult;
import org.springframework.data.domain.Pageable;

import java.util.List;

/**
 * Business operations over {@link com.distcomp.model.Issue} entities.
 */
public interface IssueService {

    IssueResponseTo create(IssueRequestTo request);

    IssueResponseTo findById(Long id);

    /**
     * @param filter   optional filtering criteria (author, title fragment, tag name)
     * @param pageable paging and sorting instructions
     * @return the matching issues and the total number of matches
     */
    PagedResult<IssueResponseTo> findAll(IssueFilter filter, Pageable pageable);

    IssueResponseTo update(Long id, IssueRequestTo request);

    /**
     * Removes an issue with its notices and with the tags that become unused.
     */
    void delete(Long id);

    List<IssueResponseTo> findByCreatorId(Long creatorId);

    List<IssueResponseTo> findByTagId(Long tagId);

    List<IssueResponseTo> findByTagName(String tagName);
}
