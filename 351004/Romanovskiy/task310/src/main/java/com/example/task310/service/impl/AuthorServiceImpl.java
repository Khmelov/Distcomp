package com.example.task310.service.impl;

import com.example.task310.domain.dto.request.AuthorRequestTo;
import com.example.task310.domain.dto.response.AuthorResponseTo;
import com.example.task310.domain.entity.Author;
import com.example.task310.exception.EntityNotFoundException;
import com.example.task310.mapper.AuthorMapper;
import com.example.task310.repository.AuthorRepository;
import com.example.task310.service.AuthorService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AuthorServiceImpl implements AuthorService {

    private final AuthorRepository authorRepository;
    private final AuthorMapper authorMapper;

    @Override
    public AuthorResponseTo create(AuthorRequestTo request) {
        Author author = authorMapper.toEntity(request);
        Author savedAuthor = authorRepository.save(author);
        return authorMapper.toResponse(savedAuthor);
    }

    @Override
    public List<AuthorResponseTo> findAll() {
        return authorRepository.findAll().stream()
                .map(authorMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public AuthorResponseTo findById(Long id) {
        Author author = authorRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Author not found with this id"));
        return authorMapper.toResponse(author);
    }

    @Override
    public AuthorResponseTo update(AuthorRequestTo request) {
        if (!authorRepository.existsById(request.getId())) {
            throw new EntityNotFoundException("Cannot update: Author not found");
        }
        Author author = authorMapper.toEntity(request);
        return authorMapper.toResponse(authorRepository.save(author));
    }

    @Override
    public void deleteById(Long id) {
        if (!authorRepository.existsById(id)) {
            throw new EntityNotFoundException("Author not found with id: " + id);
        }
        authorRepository.deleteById(id);
    }
}