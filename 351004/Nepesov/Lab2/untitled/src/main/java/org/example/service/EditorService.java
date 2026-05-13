package org.example.service;

import lombok.RequiredArgsConstructor;
import org.example.dto.EditorRequestTo;
import org.example.dto.EditorResponseTo;
import org.example.exception.EntityNotFoundException;
import org.example.mapper.EditorMapper;
import org.example.model.Editor;
import org.example.repository.EditorRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class EditorService {
    private final EditorRepository repository;
    private final EditorMapper mapper;

    public EditorResponseTo create(EditorRequestTo request) {
        Editor entity = mapper.toEntity(request);
        return mapper.toResponse(repository.save(entity));
    }

    @Transactional(readOnly = true)
    public List<EditorResponseTo> findAll(int page, int size, String sortBy) {
        return repository.findAll(PageRequest.of(page, size, Sort.by(sortBy)))
                .stream()
                .map(mapper::toResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public EditorResponseTo findById(Long id) {
        return repository.findById(id)
                .map(mapper::toResponse)
                .orElseThrow(() -> new EntityNotFoundException("Editor not found with id: " + id));
    }

    public EditorResponseTo update(EditorRequestTo request) {
        if (!repository.existsById(request.getId())) {
            throw new EntityNotFoundException("Editor not found");
        }
        return mapper.toResponse(repository.save(mapper.toEntity(request)));
    }

    public void delete(Long id) {
        if (!repository.existsById(id)) {
            throw new EntityNotFoundException("Editor not found");
        }
        repository.deleteById(id);
    }
}