package org.example.task310rest.service.impl;

import java.util.List;
import java.util.stream.Collectors;
import org.example.task310rest.dto.MessageRequestTo;
import org.example.task310rest.dto.MessageResponseTo;
import org.example.task310rest.exception.NotFoundException;
import org.example.task310rest.exception.ValidationException;
import org.example.task310rest.mapper.MessageMapper;
import org.example.task310rest.model.Message;
import org.example.task310rest.repository.MessageRepository;
import org.example.task310rest.repository.TweetRepository;
import org.example.task310rest.service.MessageService;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
public class MessageServiceImpl implements MessageService {

    private final MessageRepository messageRepository;
    private final TweetRepository tweetRepository;
    private final MessageMapper mapper;

    public MessageServiceImpl(MessageRepository messageRepository, TweetRepository tweetRepository, MessageMapper mapper) {
        this.messageRepository = messageRepository;
        this.tweetRepository = tweetRepository;
        this.mapper = mapper;
    }

    @Override
    public MessageResponseTo create(MessageRequestTo request) {
        validate(request);
        tweetRepository.findById(request.getTweetId())
                .orElseThrow(() -> new NotFoundException("Tweet not found: " + request.getTweetId()));
        Message entity = mapper.toEntity(request);
        messageRepository.save(entity);
        return mapper.toDto(entity);
    }

    @Override
    public MessageResponseTo getById(Long id) {
        Message entity = messageRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Message not found: " + id));
        return mapper.toDto(entity);
    }

    @Override
    public List<MessageResponseTo> getAll() {
        return messageRepository.findAll().stream().map(mapper::toDto).collect(Collectors.toList());
    }

    @Override
    public MessageResponseTo update(Long id, MessageRequestTo request) {
        validate(request);
        Message entity = messageRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Message not found: " + id));
        tweetRepository.findById(request.getTweetId())
                .orElseThrow(() -> new NotFoundException("Tweet not found: " + request.getTweetId()));
        mapper.updateEntityFromDto(request, entity);
        messageRepository.save(entity);
        return mapper.toDto(entity);
    }

    @Override
    public void delete(Long id) {
        Message entity = messageRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Message not found: " + id));
        messageRepository.deleteById(entity.getId());
    }

    private void validate(MessageRequestTo request) {
        if (request.getTweetId() == null || !StringUtils.hasText(request.getContent())) {
            throw new ValidationException("Message fields are invalid");
        }
    }
}


