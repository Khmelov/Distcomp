package com.distcomp.service;

import com.distcomp.dto.request.CreatorRequestTo;
import com.distcomp.dto.response.CreatorResponseTo;

import java.util.List;

/**
 * Business operations over {@link com.distcomp.model.Creator} entities.
 */
public interface CreatorService {

    /**
     * Creates a new creator.
     *
     * @param request validated payload
     * @return the stored creator
     */
    CreatorResponseTo create(CreatorRequestTo request);

    /**
     * @param id identifier of the creator
     * @return the creator with this identifier
     */
    CreatorResponseTo findById(Long id);

    /**
     * @return every stored creator
     */
    List<CreatorResponseTo> findAll();

    /**
     * Replaces the fields of an existing creator.
     *
     * @param id      identifier of the creator to change
     * @param request validated payload
     * @return the updated creator
     */
    CreatorResponseTo update(Long id, CreatorRequestTo request);

    /**
     * Removes a creator together with the issues and notices that depend on it.
     *
     * @param id identifier of the creator to remove
     */
    void delete(Long id);

    /**
     * Resolves the author of an issue.
     *
     * @param issueId identifier of the issue
     * @return the creator who wrote the issue
     */
    CreatorResponseTo findByIssueId(Long issueId);
}
