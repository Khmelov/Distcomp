package com.blog.service.impl;

import com.blog.dto.request.MessageRequestTo;
import com.blog.dto.response.MessageResponseTo;
import com.blog.mapper.MessageMapper;
import com.blog.model.Message;
import com.blog.repository.MessageRepository;
import com.blog.service.MessageService;
import com.blog.service.TopicService;
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
    private TopicService topicService;

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
        // Проверяем существование топика
        if (!topicService.existsById(request.getTopicId())) {
            throw new RuntimeException("Topic not found with id: " + request.getTopicId());
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

        // Проверяем существование топика
        if (!topicService.existsById(request.getTopicId())) {
            throw new RuntimeException("Topic not found with id: " + request.getTopicId());
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
    public List<MessageResponseTo> getByTopicId(Long topicId) {
        return messageRepository.findByTopicId(topicId).stream()
                .map(messageMapper::toResponse)
                .collect(Collectors.toList());
    }
}