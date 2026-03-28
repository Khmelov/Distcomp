package com.lizaveta.notebook.service;

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

    public NoticeService(
            final DiscussionNoticeClient discussionClient,
            final StoryRepository storyRepository) {
        this.discussionClient = discussionClient;
        this.storyRepository = storyRepository;
    }

    public NoticeResponseTo create(final NoticeRequestTo request) {
        validateStoryExists(request.storyId());
        return discussionClient.create(request);
    }

    public List<NoticeResponseTo> findAll() {
        return discussionClient.findAllAsList();
    }

    public PageResponseTo<NoticeResponseTo> findAll(final int page, final int size, final String sortBy, final String sortOrder) {
        return discussionClient.findAllPaged(page, size, sortBy, sortOrder);
    }

    public NoticeResponseTo findById(final Long id) {
        validateId(id);
        return discussionClient.findById(id);
    }

    public List<NoticeResponseTo> findByStoryId(final Long storyId) {
        validateId(storyId);
        validateStoryExists(storyId);
        return discussionClient.findByStoryId(storyId);
    }

    public NoticeResponseTo update(final Long id, final NoticeRequestTo request) {
        validateId(id);
        validateStoryExists(request.storyId());
        return discussionClient.update(id, request);
    }

    public void deleteById(final Long id) {
        validateId(id);
        discussionClient.deleteById(id);
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
