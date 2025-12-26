package com.restApp.service.impl;

import com.restApp.dto.AuthorRequestTo;
import com.restApp.dto.AuthorResponseTo;
import com.restApp.exception.BusinessException;
import com.restApp.mapper.AuthorMapper;
import com.restApp.model.Author;
import com.restApp.repository.AuthorRepository;
import com.restApp.service.AuthorService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class AuthorServiceImpl implements AuthorService {

    private final AuthorRepository authorRepository;
    private final AuthorMapper authorMapper;

    public AuthorServiceImpl(AuthorRepository authorRepository, AuthorMapper authorMapper) {
        this.authorRepository = authorRepository;
        this.authorMapper = authorMapper;
    }

    @Override
    public AuthorResponseTo create(AuthorRequestTo request) {
        if (authorRepository.findByLogin(request.getLogin()).isPresent()) {
            throw new BusinessException("Login already exists", "40301");
        }

        Author author = authorMapper.toEntity(request);
        return authorMapper.toResponse(authorRepository.save(author));
    }

    @Override
    public AuthorResponseTo update(Long id, AuthorRequestTo request) {
        Author author = authorRepository.findById(id)
                .orElseThrow(() -> new BusinessException("Author not found", "40401"));

        if (request.getLogin() != null && !request.getLogin().equals(author.getLogin())) {
            if (authorRepository.findByLogin(request.getLogin()).isPresent()) {
                throw new BusinessException("Login already exists", "40301");
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
    @Transactional(readOnly = true)
    public AuthorResponseTo findById(Long id) {
        return authorRepository.findById(id)
                .map(authorMapper::toResponse)
                .orElseThrow(() -> new BusinessException("Author not found", "40401"));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<AuthorResponseTo> findAll(Pageable pageable) {
        return authorRepository.findAll(pageable)
                .map(authorMapper::toResponse);
    }
}
