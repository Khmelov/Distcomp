package com.example.Labs.service;

import com.example.Labs.dto.request.MessageRequestTo;
import com.example.Labs.dto.response.MessageResponseTo;
import com.example.Labs.entity.Message;
import com.example.Labs.entity.Story;
import com.example.Labs.exception.ResourceNotFoundException;
import com.example.Labs.mapper.MessageMapper;
import com.example.Labs.repository.MessageRepository;
import com.example.Labs.repository.StoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class MessageService {
    private final MessageRepository repository;
    private final StoryRepository storyRepository;
    private final MessageMapper mapper;

    @Transactional
    public MessageResponseTo create(MessageRequestTo request) {
        Story story = storyRepository.findById(request.getStoryId()).orElseThrow(() -> new IllegalArgumentException("Bad Story ID"));
        Message entity = mapper.toEntity(request);
        entity.setStory(story);
        return mapper.toDto(repository.save(entity));
    }

    @Transactional(readOnly = true)
    public Page<MessageResponseTo> getAll(Pageable pageable) {
        return repository.findAll(pageable).map(mapper::toDto);
    }

    @Transactional(readOnly = true)
    public MessageResponseTo getById(Long id) {
        return mapper.toDto(repository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Not found")));
    }

    @Transactional
    public MessageResponseTo update(Long id, MessageRequestTo request) {
        Message entity = repository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Not found"));
        Story story = storyRepository.findById(request.getStoryId()).orElseThrow(() -> new IllegalArgumentException("Bad Story ID"));
        mapper.updateEntity(request, entity);
        entity.setStory(story);
        return mapper.toDto(repository.save(entity));
    }

    @Transactional
    public void delete(Long id) {
        if (!repository.existsById(id)) throw new ResourceNotFoundException("Not found");
        repository.deleteById(id);
    }
}