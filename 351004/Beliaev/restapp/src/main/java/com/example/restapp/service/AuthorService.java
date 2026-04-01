package com.example.discussion.service;

import com.example.discussion.dto.request.AuthorRequestTo;
import com.example.discussion.dto.response.AuthorResponseTo;
import com.example.discussion.exception.EntityNotFoundException;
import com.example.discussion.mapper.AuthorMapper;
import com.example.discussion.model.Author;
import com.example.discussion.model.Sticker;
import com.example.discussion.repository.AuthorRepository;
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

    private final com.example.discussion.repository.StickerRepository stickerRepository;

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
        Author saved = repository.save(author); 
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