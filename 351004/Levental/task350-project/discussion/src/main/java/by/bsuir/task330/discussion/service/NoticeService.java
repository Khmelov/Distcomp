package by.bsuir.task330.discussion.service;

import by.bsuir.task330.discussion.dto.NoticeRequestTo;
import by.bsuir.task330.discussion.dto.NoticeResponseTo;

import java.util.List;

public interface NoticeService {
    NoticeResponseTo create(NoticeRequestTo request);
    NoticeResponseTo update(NoticeRequestTo request);
    NoticeResponseTo findById(Long id);
    List<NoticeResponseTo> findAll(Integer page, Integer size, String sort, String filter, Long articleId);
    void delete(Long id);
}
