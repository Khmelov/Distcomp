package com.rest.restapp.service;

import com.rest.restapp.dto.request.AuthorRequestToDto;
import com.rest.restapp.dto.response.AuthorResponseToDto;
import com.rest.restapp.entity.Author;
import com.rest.restapp.exception.NotFoundException;
import com.rest.restapp.mapper.AuthorMapper;
import com.rest.restapp.repositry.InMemoryRepository;
import jakarta.validation.ValidationException;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AuthorService {

    InMemoryRepository repository;
    AuthorMapper mapper;

    @Transactional
    public AuthorResponseToDto createAuthor(AuthorRequestToDto requestTo) {
        validateAuthorRequest(requestTo);
        var author = mapper.toEntity(requestTo);
        var savedAuthor = repository.saveAuthor(author);
        return mapper.toResponseTo(savedAuthor);
    }

    public AuthorResponseToDto getAuthorById(Long id) {
        var author = repository.findAuthorById(id)
                .orElseThrow(() -> new NotFoundException("Author with id " + id + " not found"));
        return mapper.toResponseTo(author);
    }

    @Transactional(readOnly = true)
    public List<AuthorResponseToDto> getAllAuthors() {
        return repository.findAllAuthors().stream()
                .map(mapper::toResponseTo)
                .toList();
    }

    @Transactional
    public AuthorResponseToDto updateAuthor(Long id, AuthorRequestToDto requestTo) {
        validateAuthorRequest(requestTo);
        var existingAuthor = repository.findAuthorById(id)
                .orElseThrow(() -> new NotFoundException("Author with id " + id + " not found"));

        mapper.updateEntityFromDto(requestTo, existingAuthor);
        Author updatedAuthor = repository.saveAuthor(existingAuthor);
        return mapper.toResponseTo(updatedAuthor);
    }

    @Transactional
    public void deleteAuthor(Long id) {
        if (!repository.existsAuthorById(id)) {
            throw new NotFoundException("Author with id " + id + " not found");
        }
        repository.deleteAuthorById(id);
    }

    private void validateAuthorRequest(AuthorRequestToDto requestTo) {
        if (requestTo == null) {
            throw new ValidationException("Author request cannot be null");
        }
        if (requestTo.login() == null || requestTo.login().trim().isEmpty()) {
            throw new ValidationException("Login is required");
        }
        if (requestTo.password() == null || requestTo.password().trim().isEmpty()) {
            throw new ValidationException("Password is required");
        }
        if (requestTo.firstname() == null || requestTo.firstname().trim().isEmpty()) {
            throw new ValidationException("Firstname is required");
        }
        if (requestTo.lastname() == null || requestTo.lastname().trim().isEmpty()) {
            throw new ValidationException("Lastname is required");
        }
    }
}