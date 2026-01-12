package com.task.rest.service;

import com.task.rest.dto.AuthorRequestTo;
import com.task.rest.dto.AuthorResponseTo;
import com.task.rest.exception.DuplicateException;
import com.task.rest.exception.ResourceNotFoundException;
import com.task.rest.mapper.AuthorMapper;
import com.task.rest.model.Author;
import com.task.rest.repository.AuthorRepository;
import com.task.rest.service.cache.CacheService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class AuthorService {

    private final AuthorRepository authorRepository;
    private final AuthorMapper authorMapper;
    private final CacheService cacheService;

    public AuthorResponseTo getById(Long id) {
        log.info("Getting author by id: {}", id);

        // Try to get from cache first
        Optional<AuthorResponseTo> cached = cacheService.getAuthorFromCache(id, AuthorResponseTo.class);
        if (cached.isPresent()) {
            log.debug("Author found in cache: {}", id);
            return cached.get();
        }

        // Get from database
        Author author = authorRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Author not found with id: {}", id);
                    return new ResourceNotFoundException("Author not found with id: " + id);
                });

        AuthorResponseTo response = authorMapper.toResponseTo(author);

        // Cache the result
        cacheService.cacheAuthor(id, response);

        return response;
    }

    public List<AuthorResponseTo> getAllList() {
        log.info("Getting all authors as list");
        return authorRepository.findAll().stream()
                .map(authorMapper::toResponseTo)
                .collect(Collectors.toList());
    }

    public AuthorResponseTo create(AuthorRequestTo requestTo) {
        log.info("Creating author with login: {}", requestTo.getLogin());

        if (authorRepository.existsByLogin(requestTo.getLogin())) {
            log.error("Author with login already exists: {}", requestTo.getLogin());
            throw new DuplicateException("Author with login '" + requestTo.getLogin() + "' already exists");
        }

        Author author = authorMapper.toEntity(requestTo);
        Author saved = authorRepository.save(author);
        log.info("Author created successfully with id: {}", saved.getId());

        AuthorResponseTo response = authorMapper.toResponseTo(saved);

        // Cache the new author
        cacheService.cacheAuthor(saved.getId(), response);

        return response;
    }

    public AuthorResponseTo update(Long id, AuthorRequestTo requestTo) {
        log.info("Updating author with id: {}", id);

        Author author = authorRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Author not found with id: {}", id);
                    return new ResourceNotFoundException("Author not found with id: " + id);
                });

        if (!author.getLogin().equals(requestTo.getLogin()) &&
                authorRepository.existsByLogin(requestTo.getLogin())) {
            log.error("Author with login already exists: {}", requestTo.getLogin());
            throw new DuplicateException("Author with login '" + requestTo.getLogin() + "' already exists");
        }

        author.setLogin(requestTo.getLogin());
        author.setFirstname(requestTo.getFirstname());
        author.setLastname(requestTo.getLastname());
        author.setPassword(requestTo.getPassword());

        Author updated = authorRepository.save(author);
        log.info("Author updated successfully with id: {}", id);

        AuthorResponseTo response = authorMapper.toResponseTo(updated);

        // Update cache
        cacheService.cacheAuthor(id, response);

        return response;
    }

    public void delete(Long id) {
        log.info("Deleting author with id: {}", id);

        if (!authorRepository.existsById(id)) {
            log.error("Author not found with id: {}", id);
            throw new ResourceNotFoundException("Author not found with id: " + id);
        }

        authorRepository.deleteById(id);

        // Evict from cache
        cacheService.evictAuthor(id);

        log.info("Author deleted successfully with id: {}", id);
    }
}