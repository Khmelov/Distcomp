package by.bsuir.task330.publisher.service;

import by.bsuir.task330.publisher.client.DiscussionNoticeClient;
import by.bsuir.task330.publisher.dto.NoticeRequestTo;
import by.bsuir.task330.publisher.dto.NoticeResponseTo;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class NoticeFacadeService {

    private final DiscussionNoticeClient client;

    public NoticeFacadeService(DiscussionNoticeClient client) {
        this.client = client;
    }

    public NoticeResponseTo create(NoticeRequestTo request) {
        return client.create(request);
    }

    public NoticeResponseTo update(NoticeRequestTo request) {
        return client.update(request);
    }

    public NoticeResponseTo findById(Long id) {
        return client.findById(id);
    }

    public List<NoticeResponseTo> findAll(Integer page, Integer size, String sort, String filter, Long articleId) {
        return client.findAll(page, size, sort, filter, articleId);
    }

    public void delete(Long id) {
        client.delete(id);
    }
}
