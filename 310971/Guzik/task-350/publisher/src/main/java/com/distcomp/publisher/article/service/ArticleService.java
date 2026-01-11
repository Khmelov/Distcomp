package com.distcomp.publisher.article.service;

import com.distcomp.publisher.article.domain.Article;
import com.distcomp.publisher.article.dto.ArticleRequest;
import com.distcomp.publisher.article.dto.ArticleResponse;
import com.distcomp.publisher.article.repo.ArticleRepository;
import com.distcomp.publisher.exception.DuplicateResourceException;
import com.distcomp.publisher.exception.ResourceNotFoundException;
import com.distcomp.publisher.sticker.domain.Sticker;
import com.distcomp.publisher.sticker.repo.StickerRepository;
import com.distcomp.publisher.writer.domain.Writer;
import com.distcomp.publisher.writer.repo.WriterRepository;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ArticleService {

    private final ArticleRepository articleRepository;
    private final WriterRepository writerRepository;
    private final StickerRepository stickerRepository;

    public ArticleService(ArticleRepository articleRepository, WriterRepository writerRepository, StickerRepository stickerRepository) {
        this.articleRepository = articleRepository;
        this.writerRepository = writerRepository;
        this.stickerRepository = stickerRepository;
    }

    @Transactional
    public Optional<ArticleResponse> create(ArticleRequest request) {
        Optional<Writer> writerOpt = writerRepository.findById(request.getWriterId());
        if (writerOpt.isEmpty()) {
            throw new ResourceNotFoundException("Writer with id=" + request.getWriterId() + " not found");
        }

        if (articleRepository.existsByTitle(request.getTitle())) {
            throw new DuplicateResourceException("Article with title '" + request.getTitle() + "' already exists");
        }

        Article article = new Article();
        article.setWriter(writerOpt.get());
        article.setTitle(request.getTitle());
        article.setContent(request.getContent());
        if (request.getStickerIds() == null || request.getStickerIds().isEmpty()) {
            Long writerId = writerOpt.get().getId();
            article.setStickers(ensureDefaultStickersForWriter(writerId));
        } else {
            article.setStickers(resolveStickers(request.getStickerIds()));
        }

        return Optional.of(toResponse(articleRepository.save(article)));
    }

    public Optional<ArticleResponse> get(long id) {
        return articleRepository.findById(id).map(this::toResponse);
    }

    public List<ArticleResponse> list() {
        return articleRepository.findAll().stream().map(this::toResponse).toList();
    }

    public Optional<ArticleResponse> update(long id, ArticleRequest request) {
        Optional<Article> existingOpt = articleRepository.findById(id);
        if (existingOpt.isEmpty()) {
            return Optional.empty();
        }

        Optional<Writer> writerOpt = writerRepository.findById(request.getWriterId());
        if (writerOpt.isEmpty()) {
            return Optional.empty();
        }

        Article existing = existingOpt.get();
        existing.setWriter(writerOpt.get());
        existing.setTitle(request.getTitle());
        existing.setContent(request.getContent());
        existing.setStickers(resolveStickers(request.getStickerIds()));

        return Optional.of(toResponse(articleRepository.save(existing)));
    }

    public boolean delete(long id) {
        if (!articleRepository.existsById(id)) {
            return false;
        }
        articleRepository.deleteById(id);
        return true;
    }

    private Set<Sticker> resolveStickers(Set<Long> stickerIds) {
        if (stickerIds == null || stickerIds.isEmpty()) {
            return new HashSet<>();
        }
        List<Sticker> stickers = stickerRepository.findAllById(stickerIds);
        return new HashSet<>(stickers);
    }

    private Set<Sticker> ensureDefaultStickersForWriter(Long writerId) {
        if (writerId == null) {
            return new HashSet<>();
        }

        List<String> names = Arrays.asList(
                "red" + writerId,
                "green" + writerId,
                "blue" + writerId
        );

        List<Sticker> existing = stickerRepository.findAllByNameIn(names);
        Set<String> existingNames = new HashSet<>();
        for (Sticker s : existing) {
            if (s.getName() != null) {
                existingNames.add(s.getName());
            }
        }

        List<Sticker> toSave = new ArrayList<>();
        for (String name : names) {
            if (!existingNames.contains(name)) {
                Sticker s = new Sticker();
                s.setName(name);
                toSave.add(s);
            }
        }

        if (!toSave.isEmpty()) {
            existing.addAll(stickerRepository.saveAll(toSave));
        }

        return new HashSet<>(existing);
    }

    private ArticleResponse toResponse(Article a) {
        Set<Long> stickerIds = a.getStickers().stream()
                .map(Sticker::getId)
                .filter(id -> id != null)
                .map(Long::longValue)
                .collect(java.util.stream.Collectors.toSet());

        return new ArticleResponse(
                a.getId() != null ? a.getId() : 0,
                a.getWriter() != null && a.getWriter().getId() != null ? a.getWriter().getId() : 0,
                a.getTitle(),
                a.getContent(),
                a.getCreated(),
                a.getModified(),
                stickerIds
        );
    }
}
