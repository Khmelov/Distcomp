package com.example.discussion.service;

import com.example.discussion.dto.request.MessageRequestTo;
import com.example.discussion.dto.response.MessageResponseTo;
import com.example.discussion.entity.Message;
import com.example.discussion.entity.MessageKey;
import com.example.discussion.exception.ResourceNotFoundException;
import com.example.discussion.mapper.MessageMapper;
import com.example.discussion.repository.MessageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MessageService {
    private final MessageRepository repository;
    private final MessageMapper mapper;
    private final AtomicLong idGenerator = new AtomicLong(System.currentTimeMillis());

    public MessageResponseTo create(MessageRequestTo request) {
        // Проверка: storyId не должен быть null
        if (request.getStoryId() == null) {
            throw new IllegalArgumentException("storyId cannot be null");
        }
        long newId = idGenerator.getAndIncrement();
        Message entity = mapper.toEntityWithId(request, newId);
        return mapper.toDto(repository.save(entity));
    }

    public List<MessageResponseTo> getAll() {
        return repository.findAll().stream()
                .map(mapper::toDto)
                .collect(Collectors.toList());
    }

    public MessageResponseTo getById(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("Id cannot be null");
        }
        return repository.findAll().stream()
                .filter(m -> m.getKey().getId().equals(id))
                .findFirst()
                .map(mapper::toDto)
                .orElseThrow(() -> new ResourceNotFoundException("Message not found with id: " + id));
    }

    public List<MessageResponseTo> getByStoryId(Long storyId) {
        if (storyId == null) {
            throw new IllegalArgumentException("storyId cannot be null");
        }
        return repository.findByKeyStoryId(storyId).stream()
                .map(mapper::toDto)
                .collect(Collectors.toList());
    }

    public MessageResponseTo update(Long id, MessageRequestTo request) {
        if (id == null) {
            throw new IllegalArgumentException("Id cannot be null");
        }
        Message existing = repository.findAll().stream()
                .filter(m -> m.getKey().getId().equals(id))
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("Message not found with id: " + id));
        existing.setContent(request.getContent());
        return mapper.toDto(repository.save(existing));
    }

    public void delete(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("Id cannot be null");
        }
        Message existing = repository.findAll().stream()
                .filter(m -> m.getKey().getId().equals(id))
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("Message not found with id: " + id));
        repository.delete(existing);
    }
}