package com.example.task361.service.impl;

import com.example.task361.domain.dto.request.AuthorRequestTo;
import com.example.task361.domain.dto.response.AuthorResponseTo;
import com.example.task361.domain.entity.Author;
import com.example.task361.exception.EntityNotFoundException;
import com.example.task361.mapper.AuthorMapper;
import com.example.task361.repository.AuthorRepository;
import com.example.task361.security.Role;
import com.example.task361.service.AuthorService;
import lombok.RequiredArgsConstructor;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AuthorServiceImpl implements AuthorService {

    private final AuthorRepository authorRepository;
    private final AuthorMapper authorMapper;
    private final PasswordEncoder passwordEncoder;

    @Override
    @CachePut(value = "authors", key = "#result.id")
    public AuthorResponseTo create(AuthorRequestTo request) {
        Author author = authorMapper.toEntity(request);
        if (author.getRole() == null) {
            author.setRole(Role.CUSTOMER);
        }
        author.setPassword(encodeIfNeeded(author.getPassword()));
        Author savedAuthor = authorRepository.save(author);
        return authorMapper.toResponse(savedAuthor);
    }

    @Override
    @Cacheable(value = "authors", key = "#id")
    public AuthorResponseTo findById(Long id) {
        Author author = authorRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Author not found with this id"));
        return authorMapper.toResponse(author);
    }

    @Override
    @CachePut(value = "authors", key = "#request.id")
    public AuthorResponseTo update(AuthorRequestTo request) {
        if (!authorRepository.existsById(request.getId())) {
            throw new EntityNotFoundException("Cannot update: Author not found");
        }
        Author author = authorMapper.toEntity(request);
        if (author.getRole() == null) {
            author.setRole(Role.CUSTOMER);
        }
        author.setPassword(encodeIfNeeded(author.getPassword()));
        Author saved = authorRepository.save(author);
        return authorMapper.toResponse(saved);
    }

    @Override
    @CacheEvict(value = "authors", key = "#id")
    public void deleteById(Long id) {
        if (!authorRepository.existsById(id)) {
            throw new EntityNotFoundException("Author not found with id: " + id);
        }
        authorRepository.deleteById(id);
    }

    @Override
    public List<AuthorResponseTo> findAll(int page, int size) {
        // Создаем объект пагинации с сортировкой по ID
        Pageable pageable = PageRequest.of(page, size, Sort.by("id").ascending());
        
        // .getContent() превращает объект Page в обычный List
        return authorRepository.findAll(pageable).getContent().stream()
                .map(authorMapper::toResponse)
                .collect(Collectors.toList());
    }

    private String encodeIfNeeded(String rawOrEncodedPassword) {
        if (rawOrEncodedPassword == null || rawOrEncodedPassword.isBlank()) {
            return rawOrEncodedPassword;
        }
        // BCrypt хэш обычно начинается с $2a/$2b/$2y
        if (rawOrEncodedPassword.startsWith("$2")) {
            return rawOrEncodedPassword;
        }
        return passwordEncoder.encode(rawOrEncodedPassword);
    }
}
