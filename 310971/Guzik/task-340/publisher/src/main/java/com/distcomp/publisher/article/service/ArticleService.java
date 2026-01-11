package com.distcomp.publisher.article.service;

import com.distcomp.publisher.article.domain.Article;
import com.distcomp.publisher.article.dto.ArticleRequest;
import com.distcomp.publisher.article.dto.ArticleResponse;
import com.distcomp.publisher.article.repo.ArticleRepository;
import com.distcomp.publisher.sticker.domain.Sticker;
import com.distcomp.publisher.sticker.repo.StickerRepository;
import com.distcomp.publisher.writer.domain.Writer;
import com.distcomp.publisher.writer.repo.WriterRepository;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.springframework.stereotype.Service;

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

    public Optional<ArticleResponse> create(ArticleRequest request) {
        Optional<Writer> writerOpt = writerRepository.findById(request.getWriterId());
        if (writerOpt.isEmpty()) {
            return Optional.empty();
        }

        Article article = new Article();
        article.setWriter(writerOpt.get());
        article.setTitle(request.getTitle());
        article.setContent(request.getContent());
        article.setStickers(resolveStickers(request.getStickerIds()));

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
