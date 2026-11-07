package com.distcomp.service;

import com.distcomp.dto.filter.NoticeFilter;
import com.distcomp.dto.request.NoticeRequestTo;
import com.distcomp.dto.response.NoticeResponseTo;
import com.distcomp.pagination.PagedResult;
import org.springframework.data.domain.Pageable;

import java.util.List;

/**
 * Business operations over notices.
 *
 * <p>The publisher does not own notice storage. Every method is forwarded to the
 * discussion module, through Kafka when it is available and through REST
 * otherwise.</p>
 */
public interface NoticeService {

    NoticeResponseTo create(NoticeRequestTo request);

    NoticeResponseTo findById(Long id);

    PagedResult<NoticeResponseTo> findAll(NoticeFilter filter, Pageable pageable);

    NoticeResponseTo update(Long id, NoticeRequestTo request);

    void delete(Long id);

    List<NoticeResponseTo> findByIssueId(Long issueId);

    void deleteByIssueId(Long issueId);
}
