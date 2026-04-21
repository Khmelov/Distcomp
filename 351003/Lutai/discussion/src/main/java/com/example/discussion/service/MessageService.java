package com.example.discussion.service;

import com.example.common.dto.MessageRequestTo;
import com.example.common.dto.MessageResponseTo;
import com.example.discussion.mapper.MessageMapper;
import com.example.discussion.model.Message;
import com.example.discussion.model.MessageKey;
import com.example.discussion.repository.MessageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MessageService {

    private final MessageRepository messageRepository;
    private final MessageMapper messageMapper;

    public MessageResponseTo create(MessageRequestTo request) {
        Message message = messageMapper.toEntity(request);

        MessageKey key = new MessageKey(request.articleId(), System.currentTimeMillis());
        message.setKey(key);

        Message saved = messageRepository.save(message);
        return messageMapper.toResponse(saved);
    }

    public List<MessageResponseTo> findAll() {
        return messageRepository.findAll().stream()
                .map(messageMapper::toResponse)
                .collect(Collectors.toList());
    }

    public List<MessageResponseTo> findAllByArticleId(Long articleId) {
        return messageRepository.findAllByKeyArticleId(articleId).stream()
                .map(messageMapper::toResponse)
                .collect(Collectors.toList());
    }

    public MessageResponseTo findById(Long id) {
        return messageRepository.findByKeyId(id)
                .map(messageMapper::toResponse)
                .orElseThrow(() -> new RuntimeException("Message not found"));
    }

    public MessageResponseTo update(Long id, MessageRequestTo request) {
        Message existingMessage = messageRepository.findByKeyId(id)
                .orElseThrow(() -> new RuntimeException("Message not found"));

        existingMessage.setContent(request.content());

        Message saved = messageRepository.save(existingMessage);
        return messageMapper.toResponse(saved);
    }

    public void delete(Long id) {
        Message message = messageRepository.findByKeyId(id)
                .orElseThrow(() -> new RuntimeException("Message not found"));
        messageRepository.delete(message);
    }
}