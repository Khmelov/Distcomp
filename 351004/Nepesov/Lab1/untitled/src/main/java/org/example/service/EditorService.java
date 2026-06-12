package org.example.service;

import lombok.RequiredArgsConstructor;
import org.example.dto.EditorRequestTo;
import org.example.dto.EditorResponseTo;
import org.example.exception.EntityNotFoundException;
import org.example.mapper.EditorMapper;
import org.example.model.Editor;
import org.example.repository.EditorRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EditorService {
    private final EditorRepository repository;
    private final EditorMapper mapper;

    public EditorResponseTo create(EditorRequestTo request) {
        Editor entity = mapper.toEntity(request);
        return mapper.toResponse(repository.save(entity));
    }

    public List<EditorResponseTo> findAll() {
        return repository.findAll().stream()
                .map(mapper::toResponse)
                .collect(Collectors.toList());
    }

    public EditorResponseTo findById(Long id) {
        return repository.findById(id)
                .map(mapper::toResponse)
                .orElseThrow(() -> new EntityNotFoundException("Editor not found with id: " + id));
    }

    public EditorResponseTo update(EditorRequestTo request) {
        if (!repository.existsById(request.getId())) {
            throw new EntityNotFoundException("Cannot update: Editor not found");
        }
        Editor entity = mapper.toEntity(request);
        return mapper.toResponse(repository.save(entity));
    }

    public void delete(Long id) {
        if (!repository.existsById(id)) {
            throw new EntityNotFoundException("Cannot delete: Editor not found");
        }
        repository.deleteById(id);
    }
}