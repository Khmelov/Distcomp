package com.example.Labs.service;

import com.example.Labs.dto.request.MarkRequestTo;
import com.example.Labs.dto.response.MarkResponseTo;
import com.example.Labs.entity.Mark;
import com.example.Labs.exception.ResourceNotFoundException;
import com.example.Labs.mapper.MarkMapper;
import com.example.Labs.repository.MarkRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class MarkService {
    private final MarkRepository repository;
    private final MarkMapper mapper;

    @Transactional
    public MarkResponseTo create(MarkRequestTo request) {
        return mapper.toDto(repository.save(mapper.toEntity(request)));
    }

    @Transactional(readOnly = true)
    public Page<MarkResponseTo> getAll(Pageable pageable) {
        return repository.findAll(pageable).map(mapper::toDto);
    }

    @Transactional(readOnly = true)
    public MarkResponseTo getById(Long id) {
        return mapper.toDto(repository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Not found")));
    }

    @Transactional
    public MarkResponseTo update(Long id, MarkRequestTo request) {
        Mark entity = repository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Not found"));
        mapper.updateEntity(request, entity);
        return mapper.toDto(repository.save(entity));
    }

    @Transactional
    public void delete(Long id) {
        if (!repository.existsById(id)) throw new ResourceNotFoundException("Not found");
        repository.deleteById(id);
    }
}