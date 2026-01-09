package org.example.task330.publisher.service.impl;

import java.util.List;
import java.util.stream.Collectors;
import org.example.task330.publisher.dto.LabelRequestTo;
import org.example.task330.publisher.dto.LabelResponseTo;
import org.example.task330.publisher.exception.ConflictException;
import org.example.task330.publisher.exception.NotFoundException;
import org.example.task330.publisher.exception.ValidationException;
import org.example.task330.publisher.mapper.LabelMapper;
import org.example.task330.publisher.model.Label;
import org.example.task330.publisher.repository.LabelRepository;
import org.example.task330.publisher.service.LabelService;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@Service
public class LabelServiceImpl implements LabelService {

    private final LabelRepository repository;
    private final LabelMapper mapper;

    public LabelServiceImpl(LabelRepository repository, LabelMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    @Override
    @Transactional
    public LabelResponseTo create(LabelRequestTo request) {
        validate(request);
        
        // Check for duplicate name
        if (repository.findByName(request.getName()).isPresent()) {
            throw new ConflictException("Label with name already exists: " + request.getName());
        }
        
        Label entity = mapper.toEntity(request);
        try {
            repository.save(entity);
        } catch (DataIntegrityViolationException e) {
            throw new ConflictException("Label with name already exists: " + request.getName());
        }
        return mapper.toDto(entity);
    }

    @Override
    public LabelResponseTo getById(Long id) {
        Label entity = repository.findById(id).orElseThrow(() -> new NotFoundException("Label not found: " + id));
        return mapper.toDto(entity);
    }

    @Override
    public List<LabelResponseTo> getAll() {
        return repository.findAll().stream().map(mapper::toDto).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public LabelResponseTo update(Long id, LabelRequestTo request) {
        validate(request);
        Label entity = repository.findById(id).orElseThrow(() -> new NotFoundException("Label not found: " + id));
        
        // Check for duplicate name if name changed
        if (!entity.getName().equals(request.getName())) {
            if (repository.findByName(request.getName()).isPresent()) {
                throw new ConflictException("Label with name already exists: " + request.getName());
            }
        }
        
        mapper.updateEntityFromDto(request, entity);
        try {
            repository.save(entity);
        } catch (DataIntegrityViolationException e) {
            throw new ConflictException("Label with name already exists: " + request.getName());
        }
        return mapper.toDto(entity);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        Label entity = repository.findById(id).orElseThrow(() -> new NotFoundException("Label not found: " + id));
        repository.deleteById(entity.getId());
    }

    private void validate(LabelRequestTo request) {
        if (request == null) {
            throw new ValidationException("Label request cannot be null");
        }
        if (!StringUtils.hasText(request.getName()) || request.getName().length() < 2 || request.getName().length() > 32) {
            throw new ValidationException("Label name must be between 2 and 32 characters");
        }
    }
}

