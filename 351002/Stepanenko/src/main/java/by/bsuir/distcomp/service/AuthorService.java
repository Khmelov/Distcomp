package by.bsuir.distcomp.service;

import by.bsuir.distcomp.dto.request.AuthorRequestTo;
import by.bsuir.distcomp.dto.response.AuthorResponseTo;
import by.bsuir.distcomp.entity.Author;
import by.bsuir.distcomp.exception.DuplicateException;
import by.bsuir.distcomp.exception.ResourceNotFoundException;
import by.bsuir.distcomp.mapper.AuthorMapper;
import by.bsuir.distcomp.repository.impl.InMemoryAuthorRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class AuthorService {
    private final InMemoryAuthorRepository authorRepository;
    private final AuthorMapper authorMapper;

    public AuthorService(InMemoryAuthorRepository authorRepository, AuthorMapper authorMapper) {
        this.authorRepository = authorRepository;
        this.authorMapper = authorMapper;
    }

    public AuthorResponseTo create(AuthorRequestTo dto) {
        if (authorRepository.existsByLogin(dto.getLogin())) {
            throw new DuplicateException("Author with login '" + dto.getLogin() + "' already exists", 40901);
        }
        Author entity = authorMapper.toEntity(dto);
        Author saved = authorRepository.save(entity);
        return authorMapper.toResponseDto(saved);
    }

    public AuthorResponseTo getById(Long id) {
        Author entity = authorRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Author with id " + id + " not found", 40401));
        return authorMapper.toResponseDto(entity);
    }

    public List<AuthorResponseTo> getAll() {
        return authorRepository.findAll().stream()
                .map(authorMapper::toResponseDto)
                .collect(Collectors.toList());
    }

    public AuthorResponseTo update(AuthorRequestTo dto) {
        Author existingAuthor = authorRepository.findById(dto.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Author with id " + dto.getId() + " not found", 40402));
        
        if (!existingAuthor.getLogin().equals(dto.getLogin()) && 
            authorRepository.existsByLogin(dto.getLogin())) {
            throw new DuplicateException("Author with login '" + dto.getLogin() + "' already exists", 40902);
        }
        
        Author entity = authorMapper.toEntity(dto);
        Author updated = authorRepository.update(entity);
        return authorMapper.toResponseDto(updated);
    }

    public void deleteById(Long id) {
        if (!authorRepository.existsById(id)) {
            throw new ResourceNotFoundException("Author with id " + id + " not found", 40403);
        }
        authorRepository.deleteById(id);
    }
}
