package com.example.Labs.service;

import com.example.Labs.dto.request.EditorRequestTo;
import com.example.Labs.dto.response.EditorResponseTo;
import com.example.Labs.entity.Editor;
import com.example.Labs.exception.ResourceNotFoundException;
import com.example.Labs.mapper.EditorMapper;
import com.example.Labs.repository.EditorRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class EditorService {
    private final EditorRepository repository;
    private final EditorMapper mapper;

    @Transactional
    public EditorResponseTo create(EditorRequestTo request) {
        return mapper.toDto(repository.save(mapper.toEntity(request)));
    }

    @Transactional(readOnly = true)
    public Page<EditorResponseTo> getAll(Pageable pageable) {
        return repository.findAll(pageable).map(mapper::toDto);
    }

    @Transactional(readOnly = true)
    public EditorResponseTo getById(Long id) {
        return mapper.toDto(repository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Not found")));
    }

    @Transactional
    public EditorResponseTo update(Long id, EditorRequestTo request) {
        Editor entity = repository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Not found"));
        mapper.updateEntity(request, entity);
        return mapper.toDto(repository.save(entity));
    }

    @Transactional
    public void delete(Long id) {
        if (!repository.existsById(id)) throw new ResourceNotFoundException("Not found");
        repository.deleteById(id);
    }
}