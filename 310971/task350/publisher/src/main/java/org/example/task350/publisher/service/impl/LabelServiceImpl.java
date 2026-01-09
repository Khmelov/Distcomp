package org.example.task350.publisher.service.impl;

import java.util.List;
import java.util.stream.Collectors;
import org.example.task350.publisher.dto.LabelRequestTo;
import org.example.task350.publisher.dto.LabelResponseTo;
import org.example.task350.publisher.exception.ConflictException;
import org.example.task350.publisher.exception.NotFoundException;
import org.example.task350.publisher.exception.ValidationException;
import org.example.task350.publisher.mapper.LabelMapper;
import org.example.task350.publisher.model.Label;
import org.example.task350.publisher.repository.LabelRepository;
import org.example.task350.publisher.service.CacheService;
import org.example.task350.publisher.service.LabelService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@Service
public class LabelServiceImpl implements LabelService {

    private static final Logger log = LoggerFactory.getLogger(LabelServiceImpl.class);
    private static final String CACHE_KEY_PREFIX = "label:";
    private static final String CACHE_KEY_ALL = "label:all";

    private final LabelRepository repository;
    private final LabelMapper mapper;
    private final CacheService cacheService;

    public LabelServiceImpl(LabelRepository repository, LabelMapper mapper, CacheService cacheService) {
        this.repository = repository;
        this.mapper = mapper;
        this.cacheService = cacheService;
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
        
        LabelResponseTo response = mapper.toDto(entity);
        
        // Cache the new label
        cacheService.put(CACHE_KEY_PREFIX + response.getId(), response);
        // Invalidate all labels cache
        cacheService.delete(CACHE_KEY_ALL);
        
        return response;
    }

    @Override
    public LabelResponseTo getById(Long id) {
        String cacheKey = CACHE_KEY_PREFIX + id;
        
        // Try to get from cache first
        LabelResponseTo cached = cacheService.get(cacheKey, LabelResponseTo.class);
        if (cached != null) {
            log.debug("Cache hit for label: {}", id);
            return cached;
        }
        
        log.debug("Cache miss for label: {}, fetching from database", id);
        Label entity = repository.findById(id).orElseThrow(() -> new NotFoundException("Label not found: " + id));
        LabelResponseTo response = mapper.toDto(entity);
        
        // Cache the result
        cacheService.put(cacheKey, response);
        
        return response;
    }

    @Override
    public List<LabelResponseTo> getAll() {
        // Try to get from cache first
        @SuppressWarnings("unchecked")
        List<LabelResponseTo> cached = (List<LabelResponseTo>) cacheService.get(CACHE_KEY_ALL, List.class);
        if (cached != null) {
            log.debug("Cache hit for all labels");
            return cached;
        }
        
        log.debug("Cache miss for all labels, fetching from database");
        List<LabelResponseTo> response = repository.findAll().stream().map(mapper::toDto).collect(Collectors.toList());
        
        // Cache the result
        cacheService.put(CACHE_KEY_ALL, response);
        
        return response;
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
        
        LabelResponseTo response = mapper.toDto(entity);
        
        // Update cache
        cacheService.put(CACHE_KEY_PREFIX + id, response);
        // Invalidate all labels cache
        cacheService.delete(CACHE_KEY_ALL);
        
        return response;
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

