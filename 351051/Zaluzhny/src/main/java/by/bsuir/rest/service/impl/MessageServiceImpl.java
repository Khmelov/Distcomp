package by.bsuir.rest.service.impl;

import by.bsuir.rest.dto.request.MessageRequestTo;
import by.bsuir.rest.dto.response.MessageResponseTo;
import by.bsuir.rest.entity.Message;
import by.bsuir.rest.exception.ApiException;
import by.bsuir.rest.exception.ErrorCode;
import by.bsuir.rest.mapper.MessageMapper;
import by.bsuir.rest.repository.MessageFilter;
import by.bsuir.rest.repository.MessageRepository;
import by.bsuir.rest.repository.PageQuery;
import by.bsuir.rest.repository.StoryRepository;
import by.bsuir.rest.service.MessageService;
import by.bsuir.rest.service.RequestValidator;
import by.bsuir.rest.service.UpdateIdResolver;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class MessageServiceImpl implements MessageService {

    private final MessageRepository repository;
    private final StoryRepository storyRepository;
    private final MessageMapper mapper;
    private final RequestValidator requestValidator;
    private final UpdateIdResolver updateIdResolver;

    @Override
    public MessageResponseTo create(MessageRequestTo request) {
        requestValidator.validate(request);
        ensureStoryExists(request.getStoryId());
        Message message = mapper.toEntity(request);
        message.setId(null);
        return mapper.toResponse(repository.save(message));
    }

    @Override
    public MessageResponseTo getById(Long id) {
        return mapper.toResponse(find(id));
    }

    @Override
    public List<MessageResponseTo> getAll() {
        return repository.find(MessageFilter.empty(), PageQuery.all()).content().stream()
                .map(mapper::toResponse)
                .toList();
    }

    @Override
    public List<MessageResponseTo> findByStoryId(Long storyId) {
        if (!storyRepository.existsById(storyId)) {
            throw ApiException.notFound("Story", storyId);
        }
        return repository.find(new MessageFilter(storyId, null), PageQuery.all()).content().stream()
                .map(mapper::toResponse)
                .toList();
    }

    @Override
    public MessageResponseTo update(Long pathId, MessageRequestTo request) {
        requestValidator.validate(request);
        Long id = updateIdResolver.resolve(pathId, request.getId());
        Message message = find(id);
        ensureStoryExists(request.getStoryId());
        mapper.update(request, message);
        return mapper.toResponse(message);
    }

    @Override
    public void delete(Long id) {
        if (!repository.deleteById(id)) {
            throw ApiException.notFound("Message", id);
        }
    }

    private Message find(Long id) {
        return repository.findById(id).orElseThrow(() -> ApiException.notFound("Message", id));
    }

    private void ensureStoryExists(Long storyId) {
        if (!storyRepository.existsById(storyId)) {
            throw new ApiException(HttpStatus.BAD_REQUEST, ErrorCode.RELATED_NOT_FOUND,
                    "Story with id " + storyId + " not found");
        }
    }
}
