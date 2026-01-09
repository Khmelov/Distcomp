package org.example.task350.publisher.service.impl;

import java.util.List;
import java.util.stream.Collectors;
import org.example.task350.publisher.dto.WriterRequestTo;
import org.example.task350.publisher.dto.WriterResponseTo;
import org.example.task350.publisher.exception.ConflictException;
import org.example.task350.publisher.exception.NotFoundException;
import org.example.task350.publisher.exception.ValidationException;
import org.example.task350.publisher.mapper.WriterMapper;
import org.example.task350.publisher.model.Writer;
import org.example.task350.publisher.repository.WriterRepository;
import org.example.task350.publisher.service.CacheService;
import org.example.task350.publisher.service.WriterService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@Service
public class WriterServiceImpl implements WriterService {

    private static final Logger log = LoggerFactory.getLogger(WriterServiceImpl.class);
    private static final String CACHE_KEY_PREFIX = "writer:";
    private static final String CACHE_KEY_ALL = "writer:all";

    private final WriterRepository repository;
    private final WriterMapper mapper;
    private final CacheService cacheService;

    public WriterServiceImpl(WriterRepository repository, WriterMapper mapper, CacheService cacheService) {
        this.repository = repository;
        this.mapper = mapper;
        this.cacheService = cacheService;
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
        
        WriterResponseTo response = mapper.toDto(entity);
        
        // Cache the new writer
        cacheService.put(CACHE_KEY_PREFIX + response.getId(), response);
        // Invalidate all writers cache
        cacheService.delete(CACHE_KEY_ALL);
        
        return response;
    }

    @Override
    public WriterResponseTo getById(Long id) {
        String cacheKey = CACHE_KEY_PREFIX + id;
        
        // Try to get from cache first
        WriterResponseTo cached = cacheService.get(cacheKey, WriterResponseTo.class);
        if (cached != null) {
            log.debug("Cache hit for writer: {}", id);
            return cached;
        }
        
        log.debug("Cache miss for writer: {}, fetching from database", id);
        Writer entity = repository.findById(id).orElseThrow(() -> new NotFoundException("Writer not found: " + id));
        WriterResponseTo response = mapper.toDto(entity);
        
        // Cache the result
        cacheService.put(cacheKey, response);
        
        return response;
    }

    @Override
    public List<WriterResponseTo> getAll() {
        // Try to get from cache first
        @SuppressWarnings("unchecked")
        List<WriterResponseTo> cached = (List<WriterResponseTo>) cacheService.get(CACHE_KEY_ALL, List.class);
        if (cached != null) {
            log.debug("Cache hit for all writers");
            return cached;
        }
        
        log.debug("Cache miss for all writers, fetching from database");
        List<WriterResponseTo> response = repository.findAll().stream().map(mapper::toDto).collect(Collectors.toList());
        
        // Cache the result
        cacheService.put(CACHE_KEY_ALL, response);
        
        return response;
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
        
        WriterResponseTo response = mapper.toDto(entity);
        
        // Update cache
        cacheService.put(CACHE_KEY_PREFIX + id, response);
        // Invalidate all writers cache
        cacheService.delete(CACHE_KEY_ALL);
        
        return response;
    }

    @Override
    public void delete(Long id) {
        Writer entity = repository.findById(id).orElseThrow(() -> new NotFoundException("Writer not found: " + id));
        repository.deleteById(entity.getId());
        
        // Remove from cache
        cacheService.delete(CACHE_KEY_PREFIX + id);
        // Invalidate all writers cache
        cacheService.delete(CACHE_KEY_ALL);
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

