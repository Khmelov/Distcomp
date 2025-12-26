package com.restApp.service.impl;

import com.restApp.dto.AuthorRequestTo;
import com.restApp.dto.AuthorResponseTo;
import com.restApp.exception.BusinessException;
import com.restApp.mapper.AuthorMapper;
import com.restApp.model.Author;
import com.restApp.repository.AuthorRepository;
import com.restApp.service.AuthorService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class AuthorServiceImpl implements AuthorService {

    private final AuthorRepository authorRepository;
    private final AuthorMapper authorMapper;

    public AuthorServiceImpl(AuthorRepository authorRepository, AuthorMapper authorMapper) {
        this.authorRepository = authorRepository;
        this.authorMapper = authorMapper;
    }

    @Override
    public AuthorResponseTo create(AuthorRequestTo request) {
        // Validate
        if (request.getLogin() == null || request.getLogin().length() < 2) {
            throw new BusinessException("Login must be at least 2 characters", "40001");
        }
        if (authorRepository.findByLogin(request.getLogin()).isPresent()) {
            throw new BusinessException("Login already exists", "40901");
        }

        Author author = authorMapper.toEntity(request);
        return authorMapper.toResponse(authorRepository.save(author));
    }

    @Override
    public AuthorResponseTo update(Long id, AuthorRequestTo request) {
        Author author = authorRepository.findById(id)
                .orElseThrow(() -> new BusinessException("Author not found", "40401"));

        // Basic validation
        if (request.getLogin() != null && !request.getLogin().equals(author.getLogin())) {
            if (authorRepository.findByLogin(request.getLogin()).isPresent()) {
                throw new BusinessException("Login already exists", "40901");
            }
        }

        authorMapper.updateEntity(author, request);
        return authorMapper.toResponse(authorRepository.save(author));
    }

    @Override
    public void delete(Long id) {
        if (!authorRepository.existsById(id)) {
            throw new BusinessException("Author not found", "40401");
        }
        authorRepository.deleteById(id);
    }

    @Override
    public AuthorResponseTo findById(Long id) {
        return authorRepository.findById(id)
                .map(authorMapper::toResponse)
                .orElseThrow(() -> new BusinessException("Author not found", "40401"));
    }

    @Override
    public List<AuthorResponseTo> findAll() {
        return authorRepository.findAll().stream()
                .map(authorMapper::toResponse)
                .collect(Collectors.toList());
    }
}
