package by.bsuir.task310.service;

import by.bsuir.task310.dto.AuthorRequestTo;
import by.bsuir.task310.dto.AuthorResponseTo;
import by.bsuir.task310.exception.DuplicateException;
import by.bsuir.task310.exception.EntityNotFoundException;
import by.bsuir.task310.mapper.AuthorMapper;
import by.bsuir.task310.model.Author;
import by.bsuir.task310.repository.AuthorRepository;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AuthorService {

    private final AuthorRepository repository;
    private final AuthorMapper mapper;

    public AuthorService(AuthorRepository repository, AuthorMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    @CachePut(value = "authors", key = "#result.id")
    public AuthorResponseTo create(AuthorRequestTo requestTo) {
        if (repository.existsByLogin(requestTo.getLogin())) {
            throw new DuplicateException("Author with this login already exists");
        }

        Author author = mapper.toEntity(requestTo);
        Author saved = repository.save(author);
        return mapper.toResponseTo(saved);
    }

    public List<AuthorResponseTo> getAll() {
        return repository.findAll()
                .stream()
                .map(mapper::toResponseTo)
                .toList();
    }

    @Cacheable(value = "authors", key = "#id")
    public AuthorResponseTo getById(Long id) {
        Author author = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Author not found"));
        return mapper.toResponseTo(author);
    }

    @CachePut(value = "authors", key = "#requestTo.id")
    public AuthorResponseTo update(AuthorRequestTo requestTo) {
        if (!repository.existsById(requestTo.getId())) {
            throw new EntityNotFoundException("Author not found");
        }

        Author author = mapper.toEntity(requestTo);
        Author updated = repository.save(author);
        return mapper.toResponseTo(updated);
    }

    @CacheEvict(value = "authors", key = "#id")
    public void delete(Long id) {
        if (!repository.existsById(id)) {
            throw new EntityNotFoundException("Author not found");
        }

        repository.deleteById(id);
    }
}