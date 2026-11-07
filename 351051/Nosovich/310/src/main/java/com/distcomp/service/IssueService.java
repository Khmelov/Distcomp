package com.distcomp.service;

import com.distcomp.dto.request.IssueRequestTo;
import com.distcomp.dto.response.IssueResponseTo;

import java.util.List;

/**
 * Business operations over {@link com.distcomp.model.Issue} entities.
 */
public interface IssueService {

    /**
     * Creates a new issue and stamps its life-cycle timestamps.
     *
     * @param request validated payload
     * @return the stored issue
     */
    IssueResponseTo create(IssueRequestTo request);

    /**
     * @param id identifier of the issue
     * @return the issue with this identifier
     */
    IssueResponseTo findById(Long id);

    /**
     * @return every stored issue
     */
    List<IssueResponseTo> findAll();

    /**
     * Replaces the fields of an existing issue and refreshes the modification time.
     *
     * @param id      identifier of the issue to change
     * @param request validated payload
     * @return the updated issue
     */
    IssueResponseTo update(Long id, IssueRequestTo request);

    /**
     * Removes an issue together with the notices attached to it.
     *
     * @param id identifier of the issue to remove
     */
    void delete(Long id);

    /**
     * @param creatorId identifier of the author
     * @return every issue written by this creator
     */
    List<IssueResponseTo> findByCreatorId(Long creatorId);

    /**
     * @param tagId identifier of the tag
     * @return every issue labelled with this tag
     */
    List<IssueResponseTo> findByTagId(Long tagId);
}
