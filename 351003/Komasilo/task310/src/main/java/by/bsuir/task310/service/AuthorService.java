package by.bsuir.task310.service;

import by.bsuir.task310.dto.AuthorRequestTo;
import by.bsuir.task310.dto.AuthorResponseTo;
import by.bsuir.task310.mapper.AuthorMapper;
import by.bsuir.task310.model.Author;
import by.bsuir.task310.repository.AuthorRepository;
import org.springframework.stereotype.Service;
import by.bsuir.task310.exception.EntityNotFoundException;
import by.bsuir.task310.exception.DuplicateException;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class AuthorService {

    private final AuthorRepository repository;
    private final AuthorMapper mapper;

    public AuthorService(AuthorRepository repository, AuthorMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    // CREATE
    public AuthorResponseTo create(AuthorRequestTo requestTo) {
        if (repository.existsByLogin(requestTo.getLogin())) {
            throw new DuplicateException("Author with this login already exists");
        }

        Author author = mapper.toEntity(requestTo);
        Author saved = repository.save(author);
        return mapper.toResponseTo(saved);
    }

    // READ ALL
    public List<AuthorResponseTo> getAll() {
        return repository.findAll()
                .stream()
                .map(mapper::toResponseTo)
                .collect(Collectors.toList());
    }

    // READ BY ID
    public AuthorResponseTo getById(Long id) {
        Author author = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Author not found"));
        return mapper.toResponseTo(author);
    }

    // UPDATE
    public AuthorResponseTo update(AuthorRequestTo requestTo) {
        if (!repository.existsById(requestTo.getId())) {
            throw new EntityNotFoundException("Author not found");
        }
        Author author = mapper.toEntity(requestTo);
        Author updated = repository.save(author);
        return mapper.toResponseTo(updated);
    }

    // DELETE
    public void delete(Long id) {
        if (!repository.existsById(id)) {
            throw new EntityNotFoundException("Author not found");
        }
        repository.deleteById(id);
    }
}