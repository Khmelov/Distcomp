package com.blog.service.impl;

import com.blog.dto.request.MessageRequestTo;
import com.blog.dto.response.MessageResponseTo;
import com.blog.exception.ResourceNotFoundException;
import com.blog.mapper.MessageMapper;
import com.blog.model.Message;
import com.blog.model.Topic;
import com.blog.repository.MessageRepository;
import com.blog.repository.TopicRepository;
import com.blog.service.MessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class MessageServiceImpl implements MessageService {

    @Autowired
    private MessageRepository messageRepository;

    @Autowired
    private MessageMapper messageMapper;

    @Autowired
    private TopicRepository topicRepository;

    @Override
    public List<MessageResponseTo> getAll() {
        return messageRepository.findAll().stream()
                .map(messageMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public Page<MessageResponseTo> getAll(Pageable pageable) {
        return messageRepository.findAll(pageable)
                .map(messageMapper::toResponse);
    }

    @Override
    public MessageResponseTo getById(Long id) {
        Message message = messageRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Message not found with id: " + id));
        return messageMapper.toResponse(message);
    }

    @Override
    public MessageResponseTo create(MessageRequestTo request) {
        // Проверяем существование топика
        Topic topic = topicRepository.findById(request.getTopicId())
                .orElseThrow(() -> new ResourceNotFoundException("Topic not found with id: " + request.getTopicId()));

        Message message = messageMapper.toEntity(request);
        message.setTopic(topic);

        Message savedMessage = messageRepository.save(message);
        return messageMapper.toResponse(savedMessage);
    }

    @Override
    public MessageResponseTo update(Long id, MessageRequestTo request) {
        Message message = messageRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Message not found with id: " + id));

        // Проверяем существование топика (если изменился)
        if (!message.getTopic().getId().equals(request.getTopicId())) {
            Topic topic = topicRepository.findById(request.getTopicId())
                    .orElseThrow(() -> new ResourceNotFoundException("Topic not found with id: " + request.getTopicId()));
            message.setTopic(topic);
        }

        message.setContent(request.getContent());

        Message updatedMessage = messageRepository.save(message);
        return messageMapper.toResponse(updatedMessage);
    }

    @Override
    public void delete(Long id) {
        if (!messageRepository.existsById(id)) {
            throw new ResourceNotFoundException("Message not found with id: " + id);
        }
        messageRepository.deleteById(id);
    }

    @Override
    public boolean existsById(Long id) {
        return messageRepository.existsById(id);
    }

    @Override
    public List<MessageResponseTo> getByTopicId(Long topicId) {
        List<Message> messages = messageRepository.findByTopicId(topicId);
        return messages.stream()
                .map(messageMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public Page<MessageResponseTo> getByTopicId(Long topicId, Pageable pageable) {
        Page<Message> messages = messageRepository.findByTopicId(topicId, pageable);
        return messages.map(messageMapper::toResponse);
    }
}