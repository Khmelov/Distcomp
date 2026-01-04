package com.task.rest.service;

import com.task.rest.dto.AuthorRequestTo;
import com.task.rest.dto.AuthorResponseTo;
import com.task.rest.exception.ResourceNotFoundException;
import com.task.rest.model.Author;
import com.task.rest.repository.AuthorRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class AuthorService {

    private final AuthorRepository authorRepository;

    @Transactional(readOnly = true)
    public AuthorResponseTo getById(Long id) {
        log.info("Getting author by id: {}", id);
        Author author = authorRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Author not found with id: {}", id);
                    return new ResourceNotFoundException("Author not found with id: " + id);
                });
        return mapToResponse(author);
    }

    @Transactional(readOnly = true)
    public Page<AuthorResponseTo> getAll(Pageable pageable) {
        log.info("Getting all authors with pageable: {}", pageable);
        return authorRepository.findAll(pageable)
                .map(this::mapToResponse);
    }

    public AuthorResponseTo create(AuthorRequestTo requestTo) {
        log.info("Creating new author with login: {}", requestTo.getLogin());

        if (authorRepository.existsByLogin(requestTo.getLogin())) {
            log.error("Author with login already exists: {}", requestTo.getLogin());
            throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                    "Author with login already exists: " + requestTo.getLogin());
        }

        Author author = new Author();
        author.setLogin(requestTo.getLogin());
        author.setPassword(requestTo.getPassword());
        author.setFirstname(requestTo.getFirstname());
        author.setLastname(requestTo.getLastname());

        author = authorRepository.save(author);
        log.info("Author created with id: {}", author.getId());

        return mapToResponse(author);
    }

    public AuthorResponseTo update(Long id, AuthorRequestTo requestTo) {
        log.info("Updating author with id: {}", id);

        Author author = authorRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Author not found with id: {}", id);
                    return new ResourceNotFoundException("Author not found with id: " + id);
                });

        authorRepository.findByLogin(requestTo.getLogin()).ifPresent(existingAuthor -> {
            if (!existingAuthor.getId().equals(id)) {
                log.error("Author with login already exists: {}", requestTo.getLogin());
                throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                        "Author with login already exists: " + requestTo.getLogin());
            }
        });

        author.setLogin(requestTo.getLogin());
        author.setPassword(requestTo.getPassword());
        author.setFirstname(requestTo.getFirstname());
        author.setLastname(requestTo.getLastname());

        author = authorRepository.save(author);
        log.info("Author updated with id: {}", id);

        return mapToResponse(author);
    }

    public void delete(Long id) {
        log.info("Deleting author with id: {}", id);
        if (!authorRepository.existsById(id)) {
            log.error("Author not found with id: {}", id);
            throw new ResourceNotFoundException("Author not found with id: " + id);
        }
        authorRepository.deleteById(id);
        log.info("Author deleted with id: {}", id);
    }

    private AuthorResponseTo mapToResponse(Author author) {
        return new AuthorResponseTo(
                author.getId(),
                author.getLogin(),
                author.getFirstname(),
                author.getLastname()
        );
    }
}
