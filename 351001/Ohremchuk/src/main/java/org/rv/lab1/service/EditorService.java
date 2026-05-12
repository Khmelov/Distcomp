package org.rv.lab1.service;

import org.rv.lab1.cache.CacheNames;
import org.rv.lab1.domain.Editor;
import org.rv.lab1.domain.EditorRole;
import org.rv.lab1.dto.EditorRegisterRequestTo;
import org.rv.lab1.dto.EditorRequestTo;
import org.rv.lab1.dto.EditorResponseTo;
import org.rv.lab1.exception.ApiException;
import org.rv.lab1.mapper.EditorMapper;
import org.rv.lab1.repo.EditorRepository;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EditorService {
    private final EditorRepository repository;
    private final EditorMapper mapper;
    private final ValidationService validation;
    private final PasswordEncoder passwordEncoder;

    public EditorService(EditorRepository repository, EditorMapper mapper, ValidationService validation, PasswordEncoder passwordEncoder) {
        this.repository = repository;
        this.mapper = mapper;
        this.validation = validation;
        this.passwordEncoder = passwordEncoder;
    }

    @Caching(evict = {
            @CacheEvict(cacheNames = CacheNames.EDITOR_LIST, allEntries = true),
            @CacheEvict(cacheNames = {CacheNames.STORY, CacheNames.STORY_LIST}, allEntries = true)
    })
    public EditorResponseTo create(EditorRequestTo request) {
        validation.validate(request);
        if (repository.existsByLogin(request.login().trim())) {
            throw new ApiException(HttpStatus.FORBIDDEN, 1, "Duplicate login: " + request.login());
        }
        Editor created = repository.save(encodePasswordOn(mapper.toEntity(request), request.password()));
        return mapper.toResponse(created);
    }

    @Caching(evict = {
            @CacheEvict(cacheNames = CacheNames.EDITOR_LIST, allEntries = true),
            @CacheEvict(cacheNames = {CacheNames.STORY, CacheNames.STORY_LIST}, allEntries = true)
    })
    public EditorResponseTo register(EditorRegisterRequestTo request) {
        validation.validate(request);
        if (repository.existsByLogin(request.login().trim())) {
            throw new ApiException(HttpStatus.FORBIDDEN, 1, "Duplicate login: " + request.login());
        }
        Editor entity = mapper.toEntity(request);
        if (entity.getRole() == null) {
            entity.setRole(EditorRole.CUSTOMER);
        }
        entity.setPassword(passwordEncoder.encode(request.password().trim()));
        Editor created = repository.save(entity);
        return mapper.toResponse(created);
    }

    private Editor encodePasswordOn(Editor entity, String rawPassword) {
        entity.setPassword(passwordEncoder.encode(rawPassword.trim()));
        return entity;
    }

    @Cacheable(cacheNames = CacheNames.EDITOR_LIST, key = "'all'")
    public List<EditorResponseTo> getAll() {
        return repository.findAll().stream().map(mapper::toResponse).toList();
    }

    @Cacheable(cacheNames = CacheNames.EDITOR, key = "#id")
    public EditorResponseTo getById(long id) {
        return mapper.toResponse(findEntity(id));
    }

    @Caching(evict = {
            @CacheEvict(cacheNames = CacheNames.EDITOR, key = "#id"),
            @CacheEvict(cacheNames = CacheNames.EDITOR_LIST, allEntries = true),
            @CacheEvict(cacheNames = {CacheNames.STORY, CacheNames.STORY_LIST}, allEntries = true)
    })
    public EditorResponseTo update(long id, EditorRequestTo request) {
        validation.validate(request);
        Editor entity = findEntity(id);
        String newLogin = request.login().trim();
        if (!newLogin.equals(entity.getLogin()) && repository.existsByLogin(newLogin)) {
            throw new ApiException(HttpStatus.FORBIDDEN, 1, "Duplicate login: " + request.login());
        }
        mapper.updateEntity(request, entity);
        entity.setPassword(passwordEncoder.encode(request.password().trim()));
        Editor updated = repository.save(entity);
        return mapper.toResponse(updated);
    }

    @Caching(evict = {
            @CacheEvict(cacheNames = CacheNames.EDITOR, key = "#id"),
            @CacheEvict(cacheNames = CacheNames.EDITOR_LIST, allEntries = true),
            @CacheEvict(cacheNames = {CacheNames.STORY, CacheNames.STORY_LIST}, allEntries = true)
    })
    public void delete(long id) {
        findEntity(id);
        repository.deleteById(id);
    }

    public Editor findEntity(long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, 1, "Editor not found: " + id));
    }

    public Editor findEntity(Long id) {
        if (id == null) {
            throw new ApiException(HttpStatus.BAD_REQUEST, 12, "editorId is required");
        }
        return findEntity(id.longValue());
    }

    public java.util.Optional<Editor> findByLogin(String login) {
        if (login == null || login.isBlank()) {
            return java.util.Optional.empty();
        }
        return repository.findByLogin(login.trim());
    }

    public Editor authenticate(String login, String rawPassword) {
        if (login == null || login.isBlank() || rawPassword == null) {
            throw new ApiException(HttpStatus.UNAUTHORIZED, 1, "Invalid credentials");
        }
        Editor e = repository.findByLogin(login.trim())
                .orElseThrow(() -> new ApiException(HttpStatus.UNAUTHORIZED, 1, "Invalid credentials"));
        if (!passwordEncoder.matches(rawPassword, e.getPassword())) {
            throw new ApiException(HttpStatus.UNAUTHORIZED, 1, "Invalid credentials");
        }
        return e;
    }
}

