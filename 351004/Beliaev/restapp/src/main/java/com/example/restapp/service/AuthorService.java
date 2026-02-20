package com.example.restapp.service;

import com.example.restapp.dto.request.AuthorRequestTo;
import com.example.restapp.dto.response.AuthorResponseTo;
import com.example.restapp.exception.EntityNotFoundException;
import com.example.restapp.mapper.AuthorMapper;
import com.example.restapp.model.Author;
import com.example.restapp.repository.AuthorRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AuthorService {
    private final AuthorRepository repository;
    private final AuthorMapper mapper;

    public AuthorResponseTo create(AuthorRequestTo request) {
        Author author = mapper.toEntity(request);
        Author saved = repository.save(author);
        return mapper.toResponse(saved);
    }

    public List<AuthorResponseTo> getAll() {
        return repository.findAll().stream()
                .map(mapper::toResponse)
                .collect(Collectors.toList());
    }

    public AuthorResponseTo getById(Long id) {
        return repository.findById(id)
                .map(mapper::toResponse)
                .orElseThrow(() -> new EntityNotFoundException("Author not found with id: " + id));
    }

    public AuthorResponseTo update(Long id, AuthorRequestTo request) {
        Author author = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Author not found with id: " + id));
        mapper.updateEntityFromDto(request, author);
        repository.update(author);
        return mapper.toResponse(author);
    }

    public void delete(Long id) {
        if (!repository.deleteById(id)) {
            throw new EntityNotFoundException("Author not found with id: " + id);
        }
    }
}