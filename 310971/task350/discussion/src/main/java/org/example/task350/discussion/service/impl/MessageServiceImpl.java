package org.example.task350.discussion.service.impl;

import java.util.List;
import java.util.stream.Collectors;
import org.example.task350.discussion.dto.MessageRequestTo;
import org.example.task350.discussion.dto.MessageResponseTo;
import org.example.task350.discussion.exception.NotFoundException;
import org.example.task350.discussion.exception.ValidationException;
import org.example.task350.discussion.mapper.MessageMapper;
import org.example.task350.discussion.model.Message;
import org.example.task350.discussion.repository.MessageRepository;
import org.example.task350.discussion.service.MessageService;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
public class MessageServiceImpl implements MessageService {

    private final MessageRepository repository;
    private final MessageMapper mapper;

    public MessageServiceImpl(MessageRepository repository, MessageMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    @Override
    public MessageResponseTo create(MessageRequestTo request) {
        // Set default country if not provided
        if (request.getCountry() == null || request.getCountry().isEmpty()) {
            request.setCountry("default");
        }
        
        validate(request);
        Message entity = mapper.toEntity(request);
        
        // Create key
        Message.MessageKey key = new Message.MessageKey();
        key.setCountry(request.getCountry());
        key.setTweetId(request.getTweetId());
        key.setId(System.currentTimeMillis()); // Generate ID
        entity.setKey(key);
        
        // Set initial state to PENDING
        entity.setState(org.example.task350.discussion.model.MessageState.PENDING);
        
        repository.save(entity);
        return mapper.toDto(entity);
    }

    @Override
    public MessageResponseTo getById(String country, Long tweetId, Long id) {
        Message.MessageKey key = new Message.MessageKey();
        key.setCountry(country);
        key.setTweetId(tweetId);
        key.setId(id);
        
        Message entity = repository.findById(key)
                .orElseThrow(() -> new NotFoundException("Message not found: country=" + country + ", tweetId=" + tweetId + ", id=" + id));
        return mapper.toDto(entity);
    }

    @Override
    public List<MessageResponseTo> getAllByTweetId(String country, Long tweetId) {
        List<Message> messages = repository.findAllByCountryAndTweetId(country, tweetId);
        return messages.stream().map(mapper::toDto).collect(Collectors.toList());
    }

    @Override
    public List<MessageResponseTo> getAll() {
        return repository.findAll().stream().map(mapper::toDto).collect(Collectors.toList());
    }

    @Override
    public MessageResponseTo update(String country, Long tweetId, Long id, MessageRequestTo request) {
        Message.MessageKey key = new Message.MessageKey();
        key.setCountry(country);
        key.setTweetId(tweetId);
        key.setId(id);
        
        Message entity = repository.findById(key)
                .orElseThrow(() -> new NotFoundException("Message not found: country=" + country + ", tweetId=" + tweetId + ", id=" + id));
        
        // Set country and tweetId from path variables if not provided in request
        if (request.getCountry() == null || request.getCountry().isEmpty()) {
            request.setCountry(country);
        }
        if (request.getTweetId() == null) {
            request.setTweetId(tweetId);
        }
        
        validate(request);
        mapper.updateEntityFromDto(request, entity);
        repository.save(entity);
        return mapper.toDto(entity);
    }

    @Override
    public void delete(String country, Long tweetId, Long id) {
        Message.MessageKey key = new Message.MessageKey();
        key.setCountry(country);
        key.setTweetId(tweetId);
        key.setId(id);
        
        Message entity = repository.findById(key)
                .orElseThrow(() -> new NotFoundException("Message not found: country=" + country + ", tweetId=" + tweetId + ", id=" + id));
        repository.delete(entity);
    }

    @Override
    public Message getEntityById(String country, Long tweetId, Long id) {
        Message.MessageKey key = new Message.MessageKey();
        key.setCountry(country);
        key.setTweetId(tweetId);
        key.setId(id);
        
        return repository.findById(key)
                .orElseThrow(() -> new NotFoundException("Message not found: country=" + country + ", tweetId=" + tweetId + ", id=" + id));
    }

    @Override
    public void save(Message entity) {
        repository.save(entity);
    }

    private void validate(MessageRequestTo request) {
        if (request == null) {
            throw new ValidationException("Message request cannot be null");
        }
        if (request.getTweetId() == null) {
            throw new ValidationException("Message tweetId cannot be null");
        }
        if (!StringUtils.hasText(request.getContent()) || request.getContent().length() < 2 || request.getContent().length() > 2048) {
            throw new ValidationException("Message content must be between 2 and 2048 characters");
        }
        // Country is optional for PUT requests (can be set from existing message)
        if (request.getCountry() != null && (!StringUtils.hasText(request.getCountry()) || request.getCountry().length() < 2 || request.getCountry().length() > 64)) {
            throw new ValidationException("Message country must be between 2 and 64 characters");
        }
    }
}

