package com.task310.socialnetwork.service.impl;

import com.task310.socialnetwork.dto.request.MessageRequestTo;
import com.task310.socialnetwork.dto.response.MessageResponseTo;
import com.task310.socialnetwork.mapper.MessageMapper;
import com.task310.socialnetwork.model.Message;
import com.task310.socialnetwork.repository.MessageRepository;
import com.task310.socialnetwork.service.MessageService;
import com.task310.socialnetwork.service.TweetService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class MessageServiceImpl implements MessageService {

    @Autowired
    private MessageRepository messageRepository;

    @Autowired
    private MessageMapper messageMapper;

    @Autowired
    private TweetService tweetService;

    @Override
    public List<MessageResponseTo> getAll() {
        return messageRepository.findAll().stream()
                .map(messageMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public MessageResponseTo getById(Long id) {
        Message message = messageRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Message not found with id: " + id));
        return messageMapper.toResponse(message);
    }

    @Override
    public MessageResponseTo create(MessageRequestTo request) {
        if (!tweetService.existsById(request.getTweetId())) {
            throw new RuntimeException("Tweet not found with id: " + request.getTweetId());
        }

        Message message = messageMapper.toEntity(request);
        Message saved = messageRepository.save(message);
        return messageMapper.toResponse(saved);
    }

    @Override
    public MessageResponseTo update(Long id, MessageRequestTo request) {
        if (!messageRepository.existsById(id)) {
            throw new RuntimeException("Message not found with id: " + id);
        }

        if (!tweetService.existsById(request.getTweetId())) {
            throw new RuntimeException("Tweet not found with id: " + request.getTweetId());
        }

        Message message = messageMapper.toEntity(request);
        message.setId(id);
        Message updated = messageRepository.update(message);
        return messageMapper.toResponse(updated);
    }

    @Override
    public void delete(Long id) {
        if (!messageRepository.deleteById(id)) {
            throw new RuntimeException("Message not found with id: " + id);
        }
    }

    @Override
    public boolean existsById(Long id) {
        return messageRepository.existsById(id);
    }

    @Override
    public List<MessageResponseTo> getByTweetId(Long tweetId) {
        return messageRepository.findByTweetId(tweetId).stream()
                .map(messageMapper::toResponse)
                .collect(Collectors.toList());
    }
}