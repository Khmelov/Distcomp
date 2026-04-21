package com.example.Task310.service;

import com.example.Task310.bean.Story;
import com.example.Task310.dto.StoryRequestTo;
import com.example.Task310.dto.StoryResponseTo;
import com.example.Task310.exception.ResourceNotFoundException;
import com.example.Task310.mapper.StoryMapper;
import com.example.Task310.repository.InMemoryStoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class StoryService {

    private final InMemoryStoryRepository repository;
    private final StoryMapper mapper;

    public StoryResponseTo create(StoryRequestTo request) {
        Story story = mapper.toEntity(request);
        story.setCreated(LocalDateTime.now());
        story.setModified(LocalDateTime.now());
        return mapper.toDto(repository.save(story));
    }

    public List<StoryResponseTo> getAll() {
        return repository.findAll().stream()
                .map(mapper::toDto)
                .collect(Collectors.toList());
    }

    public StoryResponseTo getById(Long id) {
        Story story = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Story not found with id: " + id));
        return mapper.toDto(story);
    }

    public StoryResponseTo update(Long id, StoryRequestTo request) {
        Story story = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Story not found with id: " + id));

        mapper.updateEntityFromDto(request, story);
        story.setModified(LocalDateTime.now()); // Обновляем время модификации

        return mapper.toDto(repository.update(story));
    }

    public void delete(Long id) {
        if (!repository.existsById(id)) {
            throw new ResourceNotFoundException("Story not found with id: " + id);
        }
        repository.deleteById(id);
    }
}