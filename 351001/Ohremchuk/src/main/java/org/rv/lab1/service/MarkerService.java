package org.rv.lab1.service;

import org.rv.lab1.cache.CacheNames;
import org.rv.lab1.domain.Marker;
import org.rv.lab1.dto.MarkerRequestTo;
import org.rv.lab1.dto.MarkerResponseTo;
import org.rv.lab1.exception.ApiException;
import org.rv.lab1.mapper.MarkerMapper;
import org.rv.lab1.repo.MarkerRepository;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MarkerService {
    private final MarkerRepository repository;
    private final MarkerMapper mapper;
    private final ValidationService validation;

    public MarkerService(MarkerRepository repository, MarkerMapper mapper, ValidationService validation) {
        this.repository = repository;
        this.mapper = mapper;
        this.validation = validation;
    }

    @Caching(evict = {
            @CacheEvict(cacheNames = CacheNames.MARKER_LIST, allEntries = true),
            @CacheEvict(cacheNames = {CacheNames.STORY, CacheNames.STORY_LIST}, allEntries = true)
    })
    public MarkerResponseTo create(MarkerRequestTo request) {
        validation.validate(request);
        String normalized = request.name().trim();
        if (repository.existsByName(normalized)) {
            throw new ApiException(HttpStatus.FORBIDDEN, 1, "Duplicate marker name: " + normalized);
        }
        Marker created = repository.save(mapper.toEntity(request));
        return mapper.toResponse(created);
    }

    @Cacheable(cacheNames = CacheNames.MARKER_LIST, key = "'all'")
    public List<MarkerResponseTo> getAll() {
        return repository.findAll().stream().map(mapper::toResponse).toList();
    }

    @Cacheable(cacheNames = CacheNames.MARKER, key = "#id")
    public MarkerResponseTo getById(long id) {
        return mapper.toResponse(findEntity(id));
    }

    @Caching(evict = {
            @CacheEvict(cacheNames = CacheNames.MARKER, key = "#id"),
            @CacheEvict(cacheNames = CacheNames.MARKER_LIST, allEntries = true),
            @CacheEvict(cacheNames = {CacheNames.STORY, CacheNames.STORY_LIST}, allEntries = true)
    })
    public MarkerResponseTo update(long id, MarkerRequestTo request) {
        validation.validate(request);
        Marker entity = findEntity(id);
        String normalized = request.name().trim();
        if (!normalized.equals(entity.getName()) && repository.existsByName(normalized)) {
            throw new ApiException(HttpStatus.FORBIDDEN, 1, "Duplicate marker name: " + normalized);
        }
        mapper.updateEntity(request, entity);
        Marker updated = repository.save(entity);
        return mapper.toResponse(updated);
    }

    @Caching(evict = {
            @CacheEvict(cacheNames = CacheNames.MARKER, key = "#id"),
            @CacheEvict(cacheNames = CacheNames.MARKER_LIST, allEntries = true),
            @CacheEvict(cacheNames = {CacheNames.STORY, CacheNames.STORY_LIST}, allEntries = true)
    })
    public void delete(long id) {
        findEntity(id);
        repository.deleteById(id);
    }

    public Marker findEntity(long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, 1, "Marker not found: " + id));
    }

    public Marker findEntity(Long id) {
        if (id == null) {
            throw new ApiException(HttpStatus.BAD_REQUEST, 12, "markerId is required");
        }
        return findEntity(id.longValue());
    }

    public Marker findOrCreateByName(String name) {
        if (name == null || name.isBlank()) {
            throw new ApiException(HttpStatus.BAD_REQUEST, 13, "marker name is required");
        }
        String normalized = name.trim();
        if (normalized.length() < 2 || normalized.length() > 64) {
            throw new ApiException(HttpStatus.BAD_REQUEST, 14, "marker name length must be 2..64");
        }
        return repository.findByName(normalized).orElseGet(() -> repository.save(new Marker(normalized)));
    }

    /**
     * Training tests expect marker names like red<ID>, green<ID>, blue<ID>
     * where <ID> is the marker's own generated id.
     */
    @CacheEvict(cacheNames = {CacheNames.MARKER, CacheNames.MARKER_LIST}, allEntries = true)
    public Marker createColorMarker(String color) {
        if (color == null || color.isBlank()) {
            throw new ApiException(HttpStatus.BAD_REQUEST, 15, "color is required");
        }
        String c = color.trim();
        String tmpName = ("tmp-" + c + "-" + System.nanoTime());
        if (tmpName.length() > 64) {
            tmpName = tmpName.substring(0, 64);
        }
        Marker marker = repository.save(new Marker(tmpName));
        String finalName = c + marker.getId();
        if (finalName.length() > 64) {
            throw new ApiException(HttpStatus.BAD_REQUEST, 16, "generated marker name too long");
        }
        marker.setName(finalName);
        return repository.save(marker);
    }
}

