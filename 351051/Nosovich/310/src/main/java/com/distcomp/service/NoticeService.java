package com.distcomp.service;

import com.distcomp.dto.request.NoticeRequestTo;
import com.distcomp.dto.response.NoticeResponseTo;

import java.util.List;

/**
 * Business operations over {@link com.distcomp.model.Notice} entities.
 */
public interface NoticeService {

    /**
     * Creates a new notice attached to an existing issue.
     *
     * @param request validated payload
     * @return the stored notice
     */
    NoticeResponseTo create(NoticeRequestTo request);

    /**
     * @param id identifier of the notice
     * @return the notice with this identifier
     */
    NoticeResponseTo findById(Long id);

    /**
     * @return every stored notice
     */
    List<NoticeResponseTo> findAll();

    /**
     * Replaces the fields of an existing notice.
     *
     * @param id      identifier of the notice to change
     * @param request validated payload
     * @return the updated notice
     */
    NoticeResponseTo update(Long id, NoticeRequestTo request);

    /**
     * Removes a notice.
     *
     * @param id identifier of the notice to remove
     */
    void delete(Long id);

    /**
     * @param issueId identifier of the issue
     * @return every notice attached to this issue
     */
    List<NoticeResponseTo> findByIssueId(Long issueId);
}
