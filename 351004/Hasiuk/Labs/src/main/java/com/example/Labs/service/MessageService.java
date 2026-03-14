package com.example.Labs.service;

import com.example.Labs.dto.request.MessageRequestTo;
import com.example.Labs.dto.response.MessageResponseTo;
import com.example.Labs.entity.Message;
import com.example.Labs.entity.Story;
import com.example.Labs.exception.ResourceNotFoundException;
import com.example.Labs.mapper.MessageMapper;
import com.example.Labs.repository.InMemoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MessageService {
    private final InMemoryRepository<Message> messageRepository;
    private final InMemoryRepository<Story> storyRepository; // Для проверки существования Story
    private final MessageMapper mapper;

    public MessageResponseTo create(MessageRequestTo request) {
        if (storyRepository.findById(request.getStoryId()).isEmpty()) {
            throw new IllegalArgumentException("Story with id " + request.getStoryId() + " does not exist.");
        }

        Message entity = mapper.toEntity(request);
        return mapper.toDto(messageRepository.save(entity));
    }

    public List<MessageResponseTo> getAll() {
        return messageRepository.findAll().stream()
                .map(mapper::toDto)
                .collect(Collectors.toList());
    }

    public MessageResponseTo getById(Long id) {
        Message entity = messageRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Message not found with id: " + id));
        return mapper.toDto(entity);
    }

    public MessageResponseTo update(Long id, MessageRequestTo request) {
        if (storyRepository.findById(request.getStoryId()).isEmpty()) {
            throw new IllegalArgumentException("Story with id " + request.getStoryId() + " does not exist.");
        }

        Message entity = messageRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Message not found with id: " + id));

        mapper.updateEntity(request, entity);
        return mapper.toDto(messageRepository.update(entity));
    }

    public void delete(Long id) {
        if (!messageRepository.deleteById(id)) {
            throw new ResourceNotFoundException("Message not found with id: " + id);
        }
    }
}