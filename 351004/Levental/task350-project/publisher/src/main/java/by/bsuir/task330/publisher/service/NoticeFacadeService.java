package by.bsuir.task330.publisher.service;

import by.bsuir.task330.publisher.cache.RedisCacheService;
import by.bsuir.task330.publisher.client.DiscussionNoticeClient;
import by.bsuir.task330.publisher.dto.NoticeRequestTo;
import by.bsuir.task330.publisher.dto.NoticeResponseTo;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class NoticeFacadeService {

    private static final String CACHE_PREFIX = "notice:";

    private final DiscussionNoticeClient client;
    private final RedisCacheService cache;

    public NoticeFacadeService(DiscussionNoticeClient client,
                               RedisCacheService cache) {
        this.client = client;
        this.cache = cache;
    }

    // 🔹 CREATE
    public NoticeResponseTo create(NoticeRequestTo request) {
        NoticeResponseTo response = client.create(request);

        cache.save(CACHE_PREFIX + response.id(), response);

        return response;
    }

    // 🔹 UPDATE
    public NoticeResponseTo update(NoticeRequestTo request) {
        NoticeResponseTo response = client.update(request);

        cache.save(CACHE_PREFIX + response.id(), response);

        return response;
    }

    public NoticeResponseTo findById(Long id) {
        String key = CACHE_PREFIX + id;


        NoticeResponseTo cached = cache.get(key, NoticeResponseTo.class);
        if (cached != null) {
            return cached;
        }


        NoticeResponseTo response = client.findById(id);


        cache.save(key, response);

        return response;
    }


    public List<NoticeResponseTo> findAll(Integer page, Integer size, String sort, String filter, Long articleId) {
        return client.findAll(page, size, sort, filter, articleId);
    }

    // 🔹 DELETE
    public void delete(Long id) {
        client.delete(id);

        cache.delete(CACHE_PREFIX + id);
    }
}