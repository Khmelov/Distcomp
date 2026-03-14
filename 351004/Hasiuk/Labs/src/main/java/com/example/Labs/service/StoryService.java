package com.example.Labs.service;

import com.example.Labs.dto.request.StoryRequestTo;
import com.example.Labs.dto.response.StoryResponseTo;
import com.example.Labs.entity.Editor;
import com.example.Labs.entity.Story;
import com.example.Labs.exception.ResourceNotFoundException;
import com.example.Labs.mapper.StoryMapper;
import com.example.Labs.repository.InMemoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class StoryService {
    private final InMemoryRepository<Story> storyRepository;
    private final InMemoryRepository<Editor> editorRepository; // Для проверки существования Editor
    private final StoryMapper mapper;

    public StoryResponseTo create(StoryRequestTo request) {
        // Проверка существования Editor
        if (editorRepository.findById(request.getEditorId()).isEmpty()) {
            throw new IllegalArgumentException("Editor with id " + request.getEditorId() + " does not exist.");
        }

        Story entity = mapper.toEntity(request);
        LocalDateTime now = LocalDateTime.now();
        entity.setCreated(now);
        entity.setModified(now);

        return mapper.toDto(storyRepository.save(entity));
    }

    public List<StoryResponseTo> getAll() {
        return storyRepository.findAll().stream()
                .map(mapper::toDto)
                .collect(Collectors.toList());
    }

    public StoryResponseTo getById(Long id) {
        Story entity = storyRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Story not found with id: " + id));
        return mapper.toDto(entity);
    }

    public StoryResponseTo update(Long id, StoryRequestTo request) {
        // Проверка существования Editor
        if (editorRepository.findById(request.getEditorId()).isEmpty()) {
            throw new IllegalArgumentException("Editor with id " + request.getEditorId() + " does not exist.");
        }

        Story entity = storyRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Story not found with id: " + id));

        mapper.updateEntity(request, entity);
        entity.setModified(LocalDateTime.now()); // Обновляем только modified

        return mapper.toDto(storyRepository.update(entity));
    }

    public void delete(Long id) {
        if (!storyRepository.deleteById(id)) {
            throw new ResourceNotFoundException("Story not found with id: " + id);
        }
    }
}