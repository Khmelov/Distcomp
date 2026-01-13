package com.publick.service;

import com.publick.dto.AuthorRequestTo;
import com.publick.dto.AuthorResponseTo;
import com.publick.entity.Author;
import com.publick.repository.AuthorRepository;
import com.publick.service.mapper.AuthorMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class AuthorService {

    @Autowired
    private AuthorRepository authorRepository;

    @Autowired
    private AuthorMapper authorMapper;

    public AuthorResponseTo create(AuthorRequestTo request) {
        // Проверяем, существует ли уже автор с таким login
        if (authorRepository.findByLoginIgnoreCase(request.getLogin()).isPresent()) {
            throw new com.publick.exception.ForbiddenException("Author with this login already exists");
        }
        Author author = authorMapper.toEntity(request);
        Author saved = authorRepository.save(author);
        return authorMapper.toResponse(saved);
    }

    public AuthorResponseTo getById(Long id) {
        Author author = authorRepository.findById(id)
                .orElseThrow(() -> new com.publick.exception.NotFoundException("Author not found with id: " + id));
        return authorMapper.toResponse(author);
    }

    public List<AuthorResponseTo> getAll() {
        return authorRepository.findAll().stream()
                .map(authorMapper::toResponse)
                .collect(Collectors.toList());
    }

    public Page<AuthorResponseTo> getAll(Pageable pageable) {
        return authorRepository.findAll(pageable)
                .map(authorMapper::toResponse);
    }

    public AuthorResponseTo update(Long id, AuthorRequestTo request) {
        Author existing = authorRepository.findById(id)
                .orElseThrow(() -> new com.publick.exception.NotFoundException("Author not found with id: " + id));

        authorMapper.updateEntityFromDto(request, existing);
        Author saved = authorRepository.save(existing);
        return authorMapper.toResponse(saved);
    }

    public void delete(Long id) {
        if (!authorRepository.existsById(id)) {
            throw new com.publick.exception.NotFoundException("Author not found with id: " + id);
        }
        authorRepository.deleteById(id);
    }
}