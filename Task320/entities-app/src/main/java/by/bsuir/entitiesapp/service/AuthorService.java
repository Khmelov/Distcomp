package by.bsuir.entitiesapp.service;

import by.bsuir.entitiesapp.dto.AuthorRequestTo;
import by.bsuir.entitiesapp.dto.AuthorResponseTo;
import by.bsuir.entitiesapp.entity.Author;
import by.bsuir.entitiesapp.exception.BadRequestException;
import by.bsuir.entitiesapp.exception.BusinessLogicException;
import by.bsuir.entitiesapp.exception.NotFoundException;
import by.bsuir.entitiesapp.repository.AuthorRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class AuthorService {

    private final AuthorRepository repository;

    public AuthorService(AuthorRepository repository) {
        this.repository = repository;
    }

    @Transactional
    public AuthorResponseTo create(AuthorRequestTo dto) {
        validate(dto);

        if (repository.findAll().stream().anyMatch(a -> a.getLogin().equals(dto.getLogin()))) {
            throw new BusinessLogicException("Duplicate login", "40301");
        }

        Author author = new Author();
        author.setLogin(dto.getLogin());
        author.setPassword(dto.getPassword());
        author.setFirstname(dto.getFirstname());
        author.setLastname(dto.getLastname());

        Author saved = repository.save(author);
        return toResponse(saved);
    }

    public AuthorResponseTo get(Long id) {
        Author author = repository.findById(id)
                .orElseThrow(() -> new NotFoundException("Author not found", "40401"));
        return toResponse(author);
    }

    public List<AuthorResponseTo> getAll() {
        return repository.findAll().stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public AuthorResponseTo update(Long id, AuthorRequestTo dto) {
        validate(dto);

        Author author = repository.findById(id)
                .orElseThrow(() -> new NotFoundException("Author not found", "40401"));

        if (repository.findAll().stream()
                .anyMatch(a -> !a.getId().equals(id) && a.getLogin().equals(dto.getLogin()))) {
            throw new BusinessLogicException("Duplicate login", "40301");
        }

        author.setLogin(dto.getLogin());
        author.setPassword(dto.getPassword());
        author.setFirstname(dto.getFirstname());
        author.setLastname(dto.getLastname());

        return toResponse(repository.save(author));
    }

    @Transactional
    public void delete(Long id) {
        if (!repository.existsById(id)) {
            throw new NotFoundException("Author not found", "40401");
        }
        // Note: Tweets should be deleted first due to foreign key constraints
        // But since we can't inject TweetService here due to circular dependency,
        // we'll rely on the database CASCADE DELETE or handle it at the controller level
        repository.deleteById(id);
    }

    private void validate(AuthorRequestTo dto) {
        if (dto.getLogin() == null || dto.getLogin().isBlank() ||
            dto.getPassword() == null || dto.getPassword().isBlank() ||
            dto.getFirstname() == null || dto.getFirstname().isBlank() ||
            dto.getLastname() == null || dto.getLastname().isBlank()) {
            throw new BadRequestException("Invalid fields", "40001");
        }

        // Additional validation rules based on test expectations
        if (dto.getLogin().length() < 2 || dto.getLogin().length() > 64) {
            throw new BadRequestException("Invalid login length", "40001");
        }

        if (dto.getPassword().length() < 8) {
            throw new BadRequestException("Invalid password length", "40001");
        }

        if (dto.getFirstname().length() < 2 || dto.getLastname().length() < 2) {
            throw new BadRequestException("Invalid name length", "40001");
        }
    }

    private AuthorResponseTo toResponse(Author author) {
        AuthorResponseTo dto = new AuthorResponseTo();
        dto.setId(author.getId());
        dto.setLogin(author.getLogin());
        dto.setFirstname(author.getFirstname());
        dto.setLastname(author.getLastname());
        return dto;
    }
}
