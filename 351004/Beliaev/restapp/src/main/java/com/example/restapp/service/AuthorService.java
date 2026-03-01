package com.example.restapp.service;

import com.example.restapp.dto.request.AuthorRequestTo;
import com.example.restapp.dto.response.AuthorResponseTo;
import com.example.restapp.exception.EntityNotFoundException;
import com.example.restapp.mapper.AuthorMapper;
import com.example.restapp.model.Author;
import com.example.restapp.model.Sticker;
import com.example.restapp.repository.AuthorRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class AuthorService {
    private final AuthorRepository repository;
    private final AuthorMapper mapper;

    private final com.example.restapp.repository.StickerRepository stickerRepository;

    @Transactional
    public AuthorResponseTo create(AuthorRequestTo request) {
        Author author = mapper.toEntity(request);
        Author saved = repository.save(author);
        return mapper.toResponse(saved);
    }

    public List<AuthorResponseTo> getAll() {
        return repository.findAll().stream()
                .map(mapper::toResponse)
                .collect(Collectors.toList());
    }

    public AuthorResponseTo getById(Long id) {
        return repository.findById(id)
                .map(mapper::toResponse)
                .orElseThrow(() -> new EntityNotFoundException("Author not found with id: " + id));
    }

    @Transactional
    public AuthorResponseTo update(Long id, AuthorRequestTo request) {
        Author author = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Author not found with id: " + id));
        mapper.updateEntityFromDto(request, author);
        Author saved = repository.save(author); // JPA managed, but save ensures persistence
        return mapper.toResponse(saved);
    }

    @Transactional
    public void delete(Long id) {
        Author author = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Author not found with id: " + id));

        List<Sticker> stickersToDelete = author.getArticles().stream()
                .flatMap(article -> article.getStickers().stream())
                .collect(Collectors.toList());

        repository.delete(author);
        repository.flush();

        if (!stickersToDelete.isEmpty()) {
            stickerRepository.deleteAll(stickersToDelete);
            stickerRepository.flush();
        }
    }
}