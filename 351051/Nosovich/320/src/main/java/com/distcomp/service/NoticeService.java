package com.distcomp.service;

import com.distcomp.dto.filter.NoticeFilter;
import com.distcomp.dto.request.NoticeRequestTo;
import com.distcomp.dto.response.NoticeResponseTo;
import com.distcomp.pagination.PagedResult;
import org.springframework.data.domain.Pageable;

import java.util.List;

/**
 * Business operations over {@link com.distcomp.model.Notice} entities.
 */
public interface NoticeService {

    NoticeResponseTo create(NoticeRequestTo request);

    NoticeResponseTo findById(Long id);

    /**
     * @param filter   optional filtering criteria
     * @param pageable paging and sorting instructions
     * @return the matching notices and the total number of matches
     */
    PagedResult<NoticeResponseTo> findAll(NoticeFilter filter, Pageable pageable);

    NoticeResponseTo update(Long id, NoticeRequestTo request);

    void delete(Long id);

    List<NoticeResponseTo> findByIssueId(Long issueId);
}
