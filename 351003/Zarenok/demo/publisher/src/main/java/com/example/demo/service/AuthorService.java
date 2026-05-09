package com.example.demo.service;

import com.example.demo.dto.requests.AuthorRequestTo;
import com.example.demo.dto.responses.AuthorResponseTo;
import com.example.demo.exception.DuplicateException;
import com.example.demo.exception.NotFoundException;
import com.example.demo.model.Author;
import com.example.demo.repository.AuthorRepository;
import com.example.demo.specification.AuthorSpecifications;
import jakarta.transaction.Transactional;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class AuthorService {
    private final AuthorRepository authorRepository;
    private final EntityMapper mapper;
    private final PasswordEncoder passwordEncoder;

    public AuthorService(AuthorRepository authorRepository, EntityMapper mapper, PasswordEncoder passwordEncoder) {
        this.authorRepository = authorRepository;
        this.mapper = mapper;
        this.passwordEncoder = passwordEncoder;
    }

    //CREATE
    public AuthorResponseTo create(AuthorRequestTo dto){
        if (authorRepository.existsByLogin(dto.getLogin())) {
            throw new DuplicateException("Login already exists");
        }
        Author entity = mapper.toEntity(dto);
        Author saved = authorRepository.save(entity);
        return mapper.toAuthorResponse(saved);
    }
    //READ
    @Cacheable(value = "authors", key = "#id", condition = "#id != null")
    public AuthorResponseTo findById(Long id) {
        Author entity = authorRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Author not found"));

        return mapper.toAuthorResponse(entity);
    }

    public List<AuthorResponseTo> findAll(String login, String firstname, String lastname) {
        Specification<Author> spec = AuthorSpecifications.withFilters(login, firstname, lastname);
        return authorRepository.findAll(spec).stream()
                .map(mapper::toAuthorResponse)
                .collect(Collectors.toList());
    }

    public Page<AuthorResponseTo> findAll(Pageable pageable, String login, String firstname, String lastname) {
        Specification<Author> spec = AuthorSpecifications.withFilters(login, firstname, lastname);
        return authorRepository.findAll(spec, pageable)
                .map(mapper::toAuthorResponse);
    }

    public Author findByLogin(String login) {
        return authorRepository.findByLogin(login)
                .orElseThrow(() -> new NotFoundException("Author not found with login: " + login));
    }

    //UPDATE
    @CacheEvict(value = "authors", key = "#id", condition = "#id != null")
    public AuthorResponseTo update(Long id, AuthorRequestTo dto) {
        Author entity = authorRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Author not found"));

        if (!entity.getLogin().equals(dto.getLogin()) && authorRepository.existsByLogin(dto.getLogin())) {
            throw new DuplicateException("Login already exists");
        }

        if (dto.getPassword() != null && !dto.getPassword().startsWith("$2a$")) {
            dto.setPassword(passwordEncoder.encode(dto.getPassword()));
        } else if (dto.getPassword() == null) {
            dto.setPassword(entity.getPassword());
        }

        mapper.updateAuthor(dto, entity);
        Author updated = authorRepository.save(entity);

        return mapper.toAuthorResponse(updated);
    }

    @Caching(evict = {
            @CacheEvict(value = "authors", key = "#id", condition = "#id != null"),
            @CacheEvict(value = "allAuthors", allEntries = true)
    })
    public void delete(Long id) {
        try {
            authorRepository.deleteById(id);
        } catch (DataIntegrityViolationException e) {
            throw new RuntimeException("Cannot delete author because it has related issues or messages", e);
        }
    }

    public boolean isOwner(Long authorId, String currentLogin) {
        Author author = authorRepository.findById(authorId).orElse(null);
        return author != null && author.getLogin().equals(currentLogin);
    }
}
