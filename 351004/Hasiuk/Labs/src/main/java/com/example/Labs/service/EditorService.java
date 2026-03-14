package com.example.Labs.service;

import com.example.Labs.dto.request.EditorRequestTo;
import com.example.Labs.dto.response.EditorResponseTo;
import com.example.Labs.entity.Editor;
import com.example.Labs.exception.ResourceNotFoundException;
import com.example.Labs.mapper.EditorMapper;
import com.example.Labs.repository.InMemoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EditorService {
    private final InMemoryRepository<Editor> repository;
    private final EditorMapper mapper;

    public EditorResponseTo create(EditorRequestTo request) {
        Editor entity = mapper.toEntity(request);
        return mapper.toDto(repository.save(entity));
    }

    public List<EditorResponseTo> getAll() {
        return repository.findAll().stream()
                .map(mapper::toDto)
                .collect(Collectors.toList());
    }

    public EditorResponseTo getById(Long id) {
        Editor entity = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Editor not found with id: " + id));
        return mapper.toDto(entity);
    }

    public EditorResponseTo update(Long id, EditorRequestTo request) {
        Editor entity = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Editor not found with id: " + id));
        mapper.updateEntity(request, entity);
        return mapper.toDto(repository.update(entity));
    }

    public void delete(Long id) {
        if (!repository.deleteById(id)) {
            throw new ResourceNotFoundException("Editor not found with id: " + id);
        }
    }
}