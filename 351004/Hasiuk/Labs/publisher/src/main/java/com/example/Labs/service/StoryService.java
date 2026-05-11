package com.example.Labs.service;

import com.example.Labs.dto.request.StoryRequestTo;
import com.example.Labs.dto.response.StoryResponseTo;
import com.example.Labs.entity.Editor;
import com.example.Labs.entity.Mark;
import com.example.Labs.entity.Story;
import com.example.Labs.exception.ResourceNotFoundException;
import com.example.Labs.mapper.StoryMapper;
import com.example.Labs.repository.EditorRepository;
import com.example.Labs.repository.MarkRepository;
import com.example.Labs.repository.StoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class StoryService {

    private final StoryRepository repository;
    private final EditorRepository editorRepository;
    private final MarkRepository markRepository;
    private final StoryMapper mapper;

    @Transactional
    public StoryResponseTo create(StoryRequestTo request) {
        Editor editor = editorRepository.findById(request.getEditorId())
                .orElseThrow(() -> new IllegalArgumentException("Bad Editor ID"));

        Story entity = mapper.toEntity(request);
        entity.setEditor(editor);

        if (request.getMarks() != null && !request.getMarks().isEmpty()) {
            Set<Mark> markEntities = request.getMarks().stream()
                    .map(name -> markRepository.findByName(name)
                            .orElseGet(() -> {
                                Mark newMark = new Mark();
                                newMark.setName(name);
                                return markRepository.save(newMark);
                            }))
                    .collect(Collectors.toSet());
            entity.setMarks(markEntities);
        }

        entity.setCreated(LocalDateTime.now());
        entity.setModified(LocalDateTime.now());

        return mapper.toDto(repository.save(entity));
    }

    @CacheEvict(value = "stories", key = "#id") // Удаляем из кеша при удалении из БД
    @Transactional
    public void delete(Long id) {
        Story entity = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Not found"));

        Set<Mark> marksToCheck = new HashSet<>(entity.getMarks());
        repository.delete(entity);

        // Логика удаления меток, которые больше нигде не используются
        for (Mark mark : marksToCheck) {
            if (repository.countByMarksContains(mark) == 0) {
                markRepository.delete(mark);
            }
        }
    }

    @Transactional(readOnly = true)
    public Page<StoryResponseTo> getAll(Pageable pageable) {
        return repository.findAll(pageable).map(mapper::toDto);
    }

    @Cacheable(value = "stories", key = "#id") // Если есть в кеше - берем оттуда, если нет - идем в БД
    @Transactional(readOnly = true)
    public StoryResponseTo getById(Long id) {
        return mapper.toDto(repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Not found")));
    }

    @CachePut(value = "stories", key = "#id") // Обновляем запись в кеше после изменения в БД
    @Transactional
    public StoryResponseTo update(Long id, StoryRequestTo request) {
        Story entity = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Not found"));

        Editor editor = editorRepository.findById(request.getEditorId())
                .orElseThrow(() -> new IllegalArgumentException("Bad Editor ID"));

        mapper.updateEntity(request, entity);
        entity.setEditor(editor);
        entity.setModified(LocalDateTime.now());

        // Пересобираем метки
        if (request.getMarks() != null) {
            Set<Mark> markEntities = request.getMarks().stream()
                    .map(name -> markRepository.findByName(name)
                            .orElseGet(() -> {
                                Mark newMark = new Mark();
                                newMark.setName(name);
                                return markRepository.save(newMark);
                            }))
                    .collect(Collectors.toSet());
            entity.setMarks(markEntities);
        }

        return mapper.toDto(repository.save(entity));
    }
}