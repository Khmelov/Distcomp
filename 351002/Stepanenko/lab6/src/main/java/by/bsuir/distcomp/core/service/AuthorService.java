package by.bsuir.distcomp.core.service;

import by.bsuir.distcomp.core.domain.Author;
import by.bsuir.distcomp.core.exception.DuplicateException;
import by.bsuir.distcomp.core.exception.ResourceNotFoundException;
import by.bsuir.distcomp.core.mapper.AuthorMapper;
import by.bsuir.distcomp.core.repository.AuthorRepository;
import by.bsuir.distcomp.dto.request.AuthorRequestTo;
import by.bsuir.distcomp.dto.response.AuthorResponseTo;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class AuthorService {
    private final AuthorRepository authorRepository;
    private final AuthorMapper authorMapper;
    private final PasswordEncoder passwordEncoder;

    public AuthorService(AuthorRepository authorRepository, AuthorMapper authorMapper, PasswordEncoder passwordEncoder) {
        this.authorRepository = authorRepository;
        this.authorMapper = authorMapper;
        this.passwordEncoder = passwordEncoder;
    }

    public AuthorResponseTo create(AuthorRequestTo request) {
        if (authorRepository.existsByLogin(request.getLogin())) {
            throw new DuplicateException("Author already exists", 40301);
        }
        Author author = authorMapper.toEntity(request);
        author.setPassword(passwordEncoder.encode(request.getPassword()));
        if (author.getRole() == null) {
            author.setRole(Author.Role.CUSTOMER);
        }
        return authorMapper.toResponseDto(authorRepository.save(author));
    }

    public AuthorResponseTo update(AuthorRequestTo request) {
        Author existing = authorRepository.findById(request.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Author not found", 40402));

        if (!existing.getLogin().equals(request.getLogin()) && authorRepository.existsByLogin(request.getLogin())) {
            throw new DuplicateException("Login already exists", 40302);
        }

        existing.setLogin(request.getLogin());
        existing.setFirstname(request.getFirstname());
        existing.setLastname(request.getLastname());

        // Кодируем пароль при обновлении
        if (request.getPassword() != null && !request.getPassword().isBlank()) {
            existing.setPassword(passwordEncoder.encode(request.getPassword()));
        }

        if (request.getRole() != null) {
            existing.setRole(request.getRole());
        }

        return authorMapper.toResponseDto(authorRepository.save(existing));
    }

    public AuthorResponseTo getById(Long id) {
        return authorRepository.findById(id)
                .map(authorMapper::toResponseDto)
                .orElseThrow(() -> new ResourceNotFoundException("Author not found", 40401));
    }

    public Author getEntityByLogin(String login) {
        return authorRepository.findByLogin(login)
                .orElseThrow(() -> new ResourceNotFoundException("Author not found", 40401));
    }

    public List<AuthorResponseTo> getAll() {
        return authorRepository.findAll().stream().map(authorMapper::toResponseDto).collect(Collectors.toList());
    }

    public void deleteById(Long id) {
        if (!authorRepository.existsById(id)) throw new ResourceNotFoundException("Author not found", 40403);
        authorRepository.deleteById(id);
    }
}