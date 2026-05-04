package com.example.task330.service.impl;

import com.example.task330.domain.dto.request.AuthorRequestTo;
import com.example.task330.domain.dto.response.AuthorResponseTo;
import com.example.task330.domain.entity.Author;
import com.example.task330.exception.EntityNotFoundException;
import com.example.task330.mapper.AuthorMapper;
import com.example.task330.repository.AuthorRepository;
import com.example.task330.service.AuthorService;
import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
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

    @Override
    public List<AuthorResponseTo> findAll(int page, int size) {
        // Создаем объект пагинации с сортировкой по ID
        Pageable pageable = PageRequest.of(page, size, Sort.by("id").ascending());
        
        // .getContent() превращает объект Page в обычный List
        return authorRepository.findAll(pageable).getContent().stream()
                .map(authorMapper::toResponse)
                .collect(Collectors.toList());
    }
}