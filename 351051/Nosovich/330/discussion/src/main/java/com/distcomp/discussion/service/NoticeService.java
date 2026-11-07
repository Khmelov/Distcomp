package com.distcomp.discussion.service;

import com.distcomp.common.notice.NoticeDto;
import com.distcomp.discussion.dto.NoticePage;
import com.distcomp.discussion.dto.NoticeRequestTo;

/**
 * Business operations over notices stored in Cassandra.
 */
public interface NoticeService {

    NoticeDto create(NoticeRequestTo request);

    NoticeDto findById(Long id);

    NoticePage findAll(Long issueId, Integer page, Integer size, String sort);

    NoticeDto update(Long id, NoticeRequestTo request);

    void delete(Long id);

    /**
     * Removes every notice attached to the issue.
     *
     * @param issueId identifier of the issue
     * @return number of removed notices
     */
    long deleteByIssueId(Long issueId);
}
