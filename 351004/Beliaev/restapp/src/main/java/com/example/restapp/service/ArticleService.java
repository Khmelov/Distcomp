package com.example.restapp.service;

import com.example.restapp.dto.request.ArticleRequestTo;
import com.example.restapp.dto.response.ArticleResponseTo;
import com.example.restapp.exception.EntityNotFoundException;
import com.example.restapp.mapper.ArticleMapper;
import com.example.restapp.model.Article;
import com.example.restapp.model.Author;
import com.example.restapp.model.Sticker;
import com.example.restapp.repository.ArticleRepository;
import com.example.restapp.repository.AuthorRepository;
import com.example.restapp.repository.StickerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ArticleService {
    private final ArticleRepository articleRepository;
    private final AuthorRepository authorRepository;
    private final StickerRepository stickerRepository;
    private final ArticleMapper mapper;

    @Transactional
    public ArticleResponseTo create(ArticleRequestTo request) {
        Article article = mapper.toEntity(request);

        // Связываем автора
        Author author = authorRepository.findById(request.getAuthorId())
                .orElseThrow(() -> new EntityNotFoundException("Author not found with id: " + request.getAuthorId()));
        article.setAuthor(author);

        // Связываем стикеры
        if (request.getStickers() != null && !request.getStickers().isEmpty()) {
            List<Sticker> stickerEntities = request.getStickers().stream()
                    .map(name -> stickerRepository.findByName(name)
                            .orElseGet(() -> {
                                // Если стикера нет - создаем новый
                                Sticker newSticker = new Sticker();
                                newSticker.setName(name);
                                return stickerRepository.save(newSticker);
                            }))
                    .collect(Collectors.toList());

            article.setStickers(stickerEntities);
        }

        article.setCreated(LocalDateTime.now());
        article.setModified(LocalDateTime.now());

        Article saved = articleRepository.save(article);
        return mapper.toResponse(saved);
    }

    public List<ArticleResponseTo> getAll() {
        return articleRepository.findAll().stream()
                .map(mapper::toResponse)
                .collect(Collectors.toList());
    }

    public ArticleResponseTo getById(Long id) {
        return articleRepository.findById(id)
                .map(mapper::toResponse)
                .orElseThrow(() -> new EntityNotFoundException("Article not found with id: " + id));
    }

    @Transactional
    public ArticleResponseTo update(Long id, ArticleRequestTo request) {
        Article article = articleRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Article not found with id: " + id));

        mapper.updateEntityFromDto(request, article);

        if (!article.getAuthor().getId().equals(request.getAuthorId())) {
            Author author = authorRepository.findById(request.getAuthorId())
                    .orElseThrow(() -> new EntityNotFoundException("Author not found with id: " + request.getAuthorId()));
            article.setAuthor(author);
        }

        // Update stickers
        if (request.getStickers() != null) {
            List<Sticker> stickerEntities = request.getStickers().stream()
                    .map(name -> stickerRepository.findByName(name)
                            .orElseGet(() -> {
                                // Если стикера с таким именем нет — создаем и сохраняем
                                Sticker newSticker = new Sticker();
                                newSticker.setName(name);
                                return stickerRepository.save(newSticker);
                            }))
                    .collect(Collectors.toList());

            // Заменяем список стикеров статьи на новый
            article.setStickers(stickerEntities);
        }

        article.setModified(LocalDateTime.now());
        Article saved = articleRepository.save(article);
        return mapper.toResponse(saved);
    }

    @Transactional
    public void delete(Long id) {
        Article article = articleRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Article not found with id: " + id));

        List<Sticker> stickersToDelete = new ArrayList<>(article.getStickers());

        articleRepository.delete(article);
        articleRepository.flush();

        if (!stickersToDelete.isEmpty()) {
            stickerRepository.deleteAll(stickersToDelete);
            stickerRepository.flush();
        }
    }
}