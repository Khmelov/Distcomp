package com.example.app.service;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.app.dto.AuthorRequestDTO;
import com.example.app.dto.AuthorResponseDTO;
import com.example.app.exception.AppException;
import com.example.app.model.Author;
import com.example.app.repository.AuthorRepository;

import java.util.List;

@Service
public class AuthorService {
    private final AuthorRepository repository;

    public AuthorService(AuthorRepository repository) {
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

    @Transactional
    public AuthorResponseDTO createAuthor(@Valid AuthorRequestDTO request) {
        if (repository.existsByLogin(request.login())) {
            throw new AppException("Login already exists", 40902);
        }
        Author author = toEntity(request);
        Author saved = repository.save(author);
        return toResponse(saved);
    }

    @Transactional
    public AuthorResponseDTO updateAuthor(@Valid AuthorRequestDTO request) {
        if (request.id() == null) {
            throw new AppException("ID required for update", 40003);
        }
        Author existingAuthor = repository.findById(request.id())
                .orElseThrow(() -> new AppException("Author not found for update", 40404));
        
        // Проверяем, не занят ли новый логин другим пользователем
        if (!existingAuthor.getLogin().equals(request.login()) && 
            repository.existsByLogin(request.login())) {
            throw new AppException("Login already taken", 40902);
        }
        
        existingAuthor.setLogin(request.login());
        existingAuthor.setPassword(request.password());
        existingAuthor.setFirstname(request.firstname());
        existingAuthor.setLastname(request.lastname());
        
        Author updated = repository.save(existingAuthor);
        return toResponse(updated);
    }

    @Transactional
    public void deleteAuthor(@NotNull Long id) {
        if (!repository.existsById(id)) {
            throw new AppException("Author not found for deletion", 40405);
        }
        repository.deleteById(id);
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