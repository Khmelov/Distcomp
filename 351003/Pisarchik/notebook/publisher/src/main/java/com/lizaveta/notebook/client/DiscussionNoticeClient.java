package com.lizaveta.notebook.client;

import com.lizaveta.notebook.model.dto.request.NoticeRequestTo;
import com.lizaveta.notebook.model.dto.response.NoticeResponseTo;
import com.lizaveta.notebook.model.dto.response.PageResponseTo;

import java.util.List;

/**
 * HTTP client to the discussion module (Cassandra-backed notices).
 */
public interface DiscussionNoticeClient {

    NoticeResponseTo create(NoticeRequestTo request);

    List<NoticeResponseTo> findAllAsList();

    PageResponseTo<NoticeResponseTo> findAllPaged(int page, int size, String sortBy, String sortOrder);

    NoticeResponseTo findById(Long id);

    List<NoticeResponseTo> findByStoryId(Long storyId);

    NoticeResponseTo update(Long id, NoticeRequestTo request);

    void deleteById(Long id);
}
