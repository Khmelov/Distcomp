package by.bsuir.rest.service.impl;

import by.bsuir.rest.cache.ResponseCache;
import by.bsuir.rest.client.DiscussionClient;
import by.bsuir.rest.dto.request.MessageRequestTo;
import by.bsuir.rest.dto.response.MessageResponseTo;
import by.bsuir.rest.exception.ApiException;
import by.bsuir.rest.exception.ErrorCode;
import by.bsuir.rest.repository.StoryRepository;
import by.bsuir.rest.service.MessageIds;
import by.bsuir.rest.service.MessageService;
import by.bsuir.rest.service.RequestValidator;
import by.bsuir.rest.service.UpdateIdResolver;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MessageServiceImpl implements MessageService {

    private final DiscussionClient discussion;
    private final StoryRepository storyRepository;
    private final RequestValidator requestValidator;
    private final UpdateIdResolver updateIdResolver;
    private final MessageIds messageIds;
    private final ResponseCache cache;

    @Override
    public MessageResponseTo create(MessageRequestTo request) {
        requestValidator.validate(request);
        ensureStoryExists(request.getStoryId());
        request.setId(messageIds.nextId());
        return discussion.create(request);
    }

    @Override
    public MessageResponseTo getById(Long id) {
        return cache.get(ResponseCache.MESSAGE, id, MessageResponseTo.class)
                .orElseGet(() -> {
                    MessageResponseTo response = discussion.getById(id);
                    cache.put(ResponseCache.MESSAGE, id, response);
                    return response;
                });
    }

    @Override
    public List<MessageResponseTo> getAll() {
        return discussion.getAll();
    }

    @Override
    public List<MessageResponseTo> findByStoryId(Long storyId) {
        if (!storyRepository.existsById(storyId)) {
            throw ApiException.notFound("Story", storyId);
        }
        return discussion.findByStoryId(storyId);
    }

    @Override
    public MessageResponseTo update(Long pathId, MessageRequestTo request) {
        requestValidator.validate(request);
        Long id = updateIdResolver.resolve(pathId, request.getId());
        ensureStoryExists(request.getStoryId());
        MessageResponseTo response = discussion.update(id, request);
        cache.put(ResponseCache.MESSAGE, response.getId(), response);
        return response;
    }

    @Override
    public void delete(Long id) {
        discussion.delete(id);
        cache.evict(ResponseCache.MESSAGE, id);
    }

    private void ensureStoryExists(Long storyId) {
        if (!storyRepository.existsById(storyId)) {
            throw new ApiException(HttpStatus.BAD_REQUEST, ErrorCode.RELATED_NOT_FOUND,
                    "Story with id " + storyId + " not found");
        }
    }
}
