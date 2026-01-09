package org.example.task340.publisher.service.impl;

import java.util.List;
import java.util.stream.Collectors;
import org.example.task340.publisher.dto.WriterRequestTo;
import org.example.task340.publisher.dto.WriterResponseTo;
import org.example.task340.publisher.exception.ConflictException;
import org.example.task340.publisher.exception.NotFoundException;
import org.example.task340.publisher.exception.ValidationException;
import org.example.task340.publisher.mapper.WriterMapper;
import org.example.task340.publisher.model.Writer;
import org.example.task340.publisher.repository.WriterRepository;
import org.example.task340.publisher.service.WriterService;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@Service
public class WriterServiceImpl implements WriterService {

    private final WriterRepository repository;
    private final WriterMapper mapper;

    public WriterServiceImpl(WriterRepository repository, WriterMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    @Override
    @Transactional
    public WriterResponseTo create(WriterRequestTo request) {
        validate(request);
        
        // Check for duplicate login
        if (repository.findByLogin(request.getLogin()).isPresent()) {
            throw new ConflictException("Writer with login already exists: " + request.getLogin());
        }
        
        Writer entity = mapper.toEntity(request);
        try {
            repository.save(entity);
        } catch (DataIntegrityViolationException e) {
            throw new ConflictException("Writer with login already exists: " + request.getLogin());
        }
        return mapper.toDto(entity);
    }

    @Override
    public WriterResponseTo getById(Long id) {
        Writer entity = repository.findById(id).orElseThrow(() -> new NotFoundException("Writer not found: " + id));
        return mapper.toDto(entity);
    }

    @Override
    public List<WriterResponseTo> getAll() {
        return repository.findAll().stream().map(mapper::toDto).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public WriterResponseTo update(Long id, WriterRequestTo request) {
        validate(request);
        Writer entity = repository.findById(id).orElseThrow(() -> new NotFoundException("Writer not found: " + id));
        
        // Check for duplicate login if login changed
        if (!entity.getLogin().equals(request.getLogin())) {
            if (repository.findByLogin(request.getLogin()).isPresent()) {
                throw new ConflictException("Writer with login already exists: " + request.getLogin());
            }
        }
        
        mapper.updateEntityFromDto(request, entity);
        try {
            repository.save(entity);
        } catch (DataIntegrityViolationException e) {
            throw new ConflictException("Writer with login already exists: " + request.getLogin());
        }
        return mapper.toDto(entity);
    }

    @Override
    public void delete(Long id) {
        Writer entity = repository.findById(id).orElseThrow(() -> new NotFoundException("Writer not found: " + id));
        repository.deleteById(entity.getId());
    }

    private void validate(WriterRequestTo request) {
        if (request == null) {
            throw new ValidationException("Writer request cannot be null");
        }
        if (!StringUtils.hasText(request.getLogin()) || request.getLogin().length() < 2 || request.getLogin().length() > 64) {
            throw new ValidationException("Writer login must be between 2 and 64 characters");
        }
        if (!StringUtils.hasText(request.getPassword()) || request.getPassword().length() < 8 || request.getPassword().length() > 128) {
            throw new ValidationException("Writer password must be between 8 and 128 characters");
        }
        if (!StringUtils.hasText(request.getFirstname()) || request.getFirstname().length() < 2 || request.getFirstname().length() > 64) {
            throw new ValidationException("Writer firstname must be between 2 and 64 characters");
        }
        if (!StringUtils.hasText(request.getLastname()) || request.getLastname().length() < 2 || request.getLastname().length() > 64) {
            throw new ValidationException("Writer lastname must be between 2 and 64 characters");
        }
    }
}

