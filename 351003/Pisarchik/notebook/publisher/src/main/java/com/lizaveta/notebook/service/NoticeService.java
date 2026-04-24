package com.lizaveta.notebook.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.lizaveta.notebook.cache.NotebookCacheKeys;
import com.lizaveta.notebook.cache.PublisherRedisCache;
import com.lizaveta.notebook.client.DiscussionNoticeClient;
import com.lizaveta.notebook.exception.ValidationException;
import com.lizaveta.notebook.model.dto.request.NoticeRequestTo;
import com.lizaveta.notebook.model.dto.response.NoticeResponseTo;
import com.lizaveta.notebook.model.dto.response.PageResponseTo;
import com.lizaveta.notebook.repository.StoryRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class NoticeService {

    private static final int STORY_NOT_FOUND_CODE = 40002;
    private static final int INVALID_ID_CODE = 40004;

    private final DiscussionNoticeClient discussionClient;
    private final StoryRepository storyRepository;
    private final PublisherRedisCache redisCache;

    public NoticeService(
            final DiscussionNoticeClient discussionClient,
            final StoryRepository storyRepository,
            final PublisherRedisCache redisCache) {
        this.discussionClient = discussionClient;
        this.storyRepository = storyRepository;
        this.redisCache = redisCache;
    }

    public NoticeResponseTo create(final NoticeRequestTo request) {
        validateStoryExists(request.storyId());
        NoticeResponseTo created = discussionClient.create(request);
        evictNoticeCaches();
        return created;
    }

    public List<NoticeResponseTo> findAll() {
        String key = NotebookCacheKeys.noticeAll();
        return redisCache.get(key, new TypeReference<List<NoticeResponseTo>>() {
        }).orElseGet(() -> {
            List<NoticeResponseTo> loaded = discussionClient.findAllAsList();
            redisCache.put(key, loaded);
            return loaded;
        });
    }

    public PageResponseTo<NoticeResponseTo> findAll(final int page, final int size, final String sortBy, final String sortOrder) {
        String key = NotebookCacheKeys.noticePage(page, size, sortBy, sortOrder);
        return redisCache.get(key, new TypeReference<PageResponseTo<NoticeResponseTo>>() {
        }).orElseGet(() -> {
            PageResponseTo<NoticeResponseTo> loaded = discussionClient.findAllPaged(page, size, sortBy, sortOrder);
            redisCache.put(key, loaded);
            return loaded;
        });
    }

    public NoticeResponseTo findById(final Long id) {
        validateId(id);
        String key = NotebookCacheKeys.noticeById(id);
        return redisCache.get(key, NoticeResponseTo.class).orElseGet(() -> {
            NoticeResponseTo loaded = discussionClient.findById(id);
            redisCache.put(key, loaded);
            return loaded;
        });
    }

    public List<NoticeResponseTo> findByStoryId(final Long storyId) {
        validateId(storyId);
        validateStoryExists(storyId);
        String key = NotebookCacheKeys.noticeByStory(storyId);
        return redisCache.get(key, new TypeReference<List<NoticeResponseTo>>() {
        }).orElseGet(() -> {
            List<NoticeResponseTo> loaded = discussionClient.findByStoryId(storyId);
            redisCache.put(key, loaded);
            return loaded;
        });
    }

    public NoticeResponseTo update(final Long id, final NoticeRequestTo request) {
        validateId(id);
        validateStoryExists(request.storyId());
        NoticeResponseTo updated = discussionClient.update(id, request);
        evictNoticeCaches();
        return updated;
    }

    public void deleteById(final Long id) {
        validateId(id);
        discussionClient.deleteById(id);
        evictNoticeCaches();
    }

    private void evictNoticeCaches() {
        redisCache.evictKeyPattern(NotebookCacheKeys.NOTICE_PREFIX + "*");
    }

    private void validateId(final Long id) {
        if (id == null || id <= 0) {
            throw new ValidationException("Id must be a positive number", INVALID_ID_CODE);
        }
    }

    private void validateStoryExists(final Long storyId) {
        if (!storyRepository.existsById(storyId)) {
            throw new ValidationException("Story not found with id: " + storyId, STORY_NOT_FOUND_CODE);
        }
    }
}
