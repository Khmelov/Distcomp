package com.example.app.service;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.stereotype.Service;

import com.example.app.dto.AuthorRequestDTO;
import com.example.app.dto.AuthorResponseDTO;
import com.example.app.exception.AppException;
import com.example.app.model.Author;
import com.example.app.repository.InMemoryAuthorRepository;

import java.util.List;

@Service
public class AuthorService {
    private final InMemoryAuthorRepository repository;

    public AuthorService(InMemoryAuthorRepository repository) {
        this.repository = repository;
    }

    public List<AuthorResponseDTO> getAllAuthors() {
        return repository.findAll().stream()
                .map(this::toResponse)
                .toList();
    }

    public AuthorResponseDTO getAuthorById(@NotNull Long id) {
        return repository.findById(id)
                .map(this::toResponse)
                .orElseThrow(() -> new AppException("Author not found", 40401));
    }

    public AuthorResponseDTO createAuthor(@Valid AuthorRequestDTO request) {
        if (repository.findByLogin(request.login()) != null) {
            throw new AppException("Login already exists", 40902);
        }
        Author author = toEntity(request);
        Author saved = repository.save(author);
        return toResponse(saved);
    }

    public AuthorResponseDTO updateAuthor(@Valid AuthorRequestDTO request) {
        if (request.id() == null) {
            throw new AppException("ID required for update", 40003);
        }
        if (!repository.findById(request.id()).isPresent()) {
            throw new AppException("Author not found for update", 40404);
        }
        Author author = toEntity(request);
        Author updated = repository.save(author);
        return toResponse(updated);
    }

    public void deleteAuthor(@NotNull Long id) {
        if (!repository.deleteById(id)) {
            throw new AppException("Author not found for deletion", 40405);
        }
    }

    private Author toEntity(AuthorRequestDTO dto) {
        Author author = new Author();
        author.setId(dto.id());
        author.setLogin(dto.login());
        author.setPassword(dto.password());
        author.setFirstname(dto.firstname());
        author.setLastname(dto.lastname());
        return author;
    }

    private AuthorResponseDTO toResponse(Author author) {
        return new AuthorResponseDTO(
                author.getId(),
                author.getLogin(),
                author.getPassword(),
                author.getFirstname(),
                author.getLastname()
        );
    }
}