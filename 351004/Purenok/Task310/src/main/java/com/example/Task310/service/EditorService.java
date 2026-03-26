package com.example.Task310.service;

import com.example.Task310.bean.Editor;
import com.example.Task310.dto.EditorRequestTo;
import com.example.Task310.dto.EditorResponseTo;
import com.example.Task310.exception.ResourceNotFoundException;
import com.example.Task310.mapper.EditorMapper;
import com.example.Task310.repository.InMemoryEditorRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EditorService {

    private final InMemoryEditorRepository repository;
    private final EditorMapper mapper;

    public EditorResponseTo create(EditorRequestTo request) {
        Editor editor = mapper.toEntity(request);
        return mapper.toDto(repository.save(editor));
    }

    public List<EditorResponseTo> getAll() {
        return repository.findAll().stream()
                .map(mapper::toDto)
                .collect(Collectors.toList());
    }

    public EditorResponseTo getById(Long id) {
        Editor editor = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Editor not found with id: " + id));
        return mapper.toDto(editor);
    }

    public EditorResponseTo update(Long id, EditorRequestTo request) {
        Editor editor = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Editor not found with id: " + id));

        mapper.updateEntityFromDto(request, editor);
        return mapper.toDto(repository.update(editor));
    }

    public void delete(Long id) {
        if (!repository.existsById(id)) {
            throw new ResourceNotFoundException("Editor not found with id: " + id);
        }
        repository.deleteById(id);
    }
}