package com.example.discussion.service;

import com.example.discussion.dto.request.MessageRequestTo;
import com.example.discussion.dto.response.MessageResponseTo;
import com.example.discussion.entity.Message;
import com.example.discussion.entity.MessageKey;
import com.example.discussion.entity.MessageState;
import com.example.discussion.exception.ResourceNotFoundException;
import com.example.discussion.mapper.MessageMapper;
import com.example.discussion.repository.MessageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

@Service
@RequiredArgsConstructor
public class MessageService {
    private final MessageRepository repository;
    private final MessageMapper mapper;
    private final AtomicLong idGenerator = new AtomicLong(System.currentTimeMillis());

    public MessageResponseTo create(MessageRequestTo request) {
        long newId = idGenerator.getAndIncrement();
        Message entity = new Message();
        MessageKey key = new MessageKey();
        key.setStoryId(request.getStoryId()); // Берем строго из запроса
        key.setId(newId);
        entity.setKey(key);
        entity.setContent(request.getContent());
        entity.setState(MessageState.PENDING);
        return mapper.toDto(repository.save(entity));
    }

    public MessageResponseTo updateState(Long storyId, Long id, MessageState state) {
        MessageKey key = new MessageKey();
        key.setStoryId(storyId);
        key.setId(id);
        Message existing = repository.findById(key)
                .orElseThrow(() -> new ResourceNotFoundException("Not found: " + id));
        existing.setState(state);
        return mapper.toDto(repository.save(existing));
    }

    public List<MessageResponseTo> getAll() {
        return repository.findAll().stream().map(mapper::toDto).toList();
    }

    public MessageResponseTo getById(Long id) {
        return repository.findAll().stream()
                .filter(m -> m.getKey().getId().equals(id))
                .findFirst()
                .map(mapper::toDto)
                .orElseThrow(() -> new ResourceNotFoundException("Not found id: " + id));
    }

    public List<MessageResponseTo> getByStoryId(Long storyId) {
        return repository.findByKeyStoryId(storyId).stream().map(mapper::toDto).toList();
    }

    public void delete(Long id) {
        Message existing = repository.findAll().stream()
                .filter(m -> m.getKey().getId().equals(id))
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("Message not found id: " + id));
        repository.delete(existing);
    }

    public MessageResponseTo update(Long id, MessageRequestTo request) {
        Message existing = repository.findAll().stream()
                .filter(m -> m.getKey().getId().equals(id))
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("Not found id: " + id));
        existing.setContent(request.getContent());
        return mapper.toDto(repository.save(existing));
    }
}