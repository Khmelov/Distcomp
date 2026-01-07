package com.task310.blogplatform.service;

import com.task310.blogplatform.dto.LabelRequestTo;
import com.task310.blogplatform.dto.LabelResponseTo;
import com.task310.blogplatform.exception.DuplicateException;
import com.task310.blogplatform.exception.EntityNotFoundException;
import com.task310.blogplatform.exception.ValidationException;
import com.task310.blogplatform.mapper.LabelMapper;
import com.task310.blogplatform.model.Label;
import com.task310.blogplatform.repository.LabelRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class LabelService {
    private final LabelRepository repository;
    private final LabelMapper mapper;

    @Autowired
    public LabelService(LabelRepository repository, LabelMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    public LabelResponseTo create(LabelRequestTo dto) {
        validateLabelRequest(dto);
        
        // Check for duplicate name
        if (repository.findByName(dto.getName().trim()).isPresent()) {
            throw new DuplicateException("Label with name '" + dto.getName() + "' already exists");
        }
        
        Label label = mapper.toEntity(dto);
        Label saved = repository.save(label);
        return mapper.toResponseDto(saved);
    }

    public List<LabelResponseTo> findAll() {
        return mapper.toResponseDtoList(repository.findAll());
    }

    public LabelResponseTo findById(Long id) {
        if (id == null || id <= 0) {
            throw new ValidationException("Invalid label id");
        }
        return repository.findById(id)
                .map(mapper::toResponseDto)
                .orElseThrow(() -> new EntityNotFoundException("Label not found with id: " + id));
    }

    public LabelResponseTo update(Long id, LabelRequestTo dto) {
        if (id == null || id <= 0) {
            throw new ValidationException("Invalid label id");
        }
        validateLabelRequest(dto);
        Label existing = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Label not found with id: " + id));
        
        // Check for duplicate name (excluding current label)
        repository.findByName(dto.getName().trim())
                .ifPresent(label -> {
                    if (!label.getId().equals(id)) {
                        throw new DuplicateException("Label with name '" + dto.getName() + "' already exists");
                    }
                });
        
        mapper.updateEntityFromDto(dto, existing);
        Label updated = repository.save(existing);
        return mapper.toResponseDto(updated);
    }

    public void delete(Long id) {
        if (id == null || id <= 0) {
            throw new ValidationException("Invalid label id");
        }
        if (!repository.existsById(id)) {
            throw new EntityNotFoundException("Label not found with id: " + id);
        }
        repository.deleteById(id);
    }

    private void validateLabelRequest(LabelRequestTo dto) {
        if (dto == null) {
            throw new ValidationException("Label data is required");
        }
        if (dto.getId() != null) {
            throw new ValidationException("Id must not be provided in request body");
        }
        if (dto.getName() == null || dto.getName().trim().isEmpty()) {
            throw new ValidationException("Name is required");
        }
        if (dto.getName().trim().length() < 2) {
            throw new ValidationException("Name must be at least 2 characters long");
        }
        if (dto.getName().trim().length() > 32) {
            throw new ValidationException("Name must not exceed 32 characters");
        }
    }
}

