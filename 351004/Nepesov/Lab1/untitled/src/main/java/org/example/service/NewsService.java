package org.example.service;

import lombok.RequiredArgsConstructor;
import org.example.dto.NewsRequestTo;
import org.example.dto.NewsResponseTo;
import org.example.exception.EntityNotFoundException;
import org.example.mapper.NewsMapper;
import org.example.model.News;
import org.example.repository.EditorRepository;
import org.example.repository.NewsRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class NewsService {
    private final NewsRepository repository;
    private final EditorRepository editorRepository; // Добавили проверку автора
    private final NewsMapper mapper;

    public NewsResponseTo create(NewsRequestTo request) {
        if (!editorRepository.existsById(request.getEditorId())) {
            throw new EntityNotFoundException("Editor not found with id: " + request.getEditorId());
        }
        News entity = mapper.toEntity(request);
        entity.setCreated(LocalDateTime.now());
        entity.setModified(LocalDateTime.now());
        return mapper.toResponse(repository.save(entity));
    }

    public List<NewsResponseTo> findAll() {
        return repository.findAll().stream()
                .map(mapper::toResponse)
                .collect(Collectors.toList());
    }

    public NewsResponseTo findById(Long id) {
        return repository.findById(id)
                .map(mapper::toResponse)
                .orElseThrow(() -> new EntityNotFoundException("News not found with id: " + id));
    }

    public NewsResponseTo update(NewsRequestTo request) {
        News existing = repository.findById(request.getId())
                .orElseThrow(() -> new EntityNotFoundException("News not found"));

        if (!editorRepository.existsById(request.getEditorId())) {
            throw new EntityNotFoundException("Editor not found");
        }

        News entity = mapper.toEntity(request);
        entity.setCreated(existing.getCreated());
        entity.setModified(LocalDateTime.now());
        return mapper.toResponse(repository.save(entity));
    }

    public void delete(Long id) {
        if (!repository.existsById(id)) {
            throw new EntityNotFoundException("News not found");
        }
        repository.deleteById(id);
    }
}