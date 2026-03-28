package by.distcomp.app.service;

import by.distcomp.app.dto.ArticleRequestTo;
import by.distcomp.app.dto.ArticleResponseTo;
import by.distcomp.app.exception.AssociationNotFoundException;
import by.distcomp.app.exception.DuplicateEntityException;
import by.distcomp.app.exception.ResourceNotFoundException;
import by.distcomp.app.mapper.ArticleMapper;
import by.distcomp.app.model.Article;
import by.distcomp.app.model.Sticker;
import by.distcomp.app.model.User;
import by.distcomp.app.repository.ArticleRepository;
import by.distcomp.app.repository.NoteRepository;
import by.distcomp.app.repository.StickerRepository;
import by.distcomp.app.repository.UserRepository;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class ArticleService {

    private final ArticleRepository articleRepository;
    private final StickerRepository stickerRepository;
    private final NoteRepository noteRepository;
    private final UserRepository userRepository;
    private final ArticleMapper articleMapper;

    public ArticleService(
            ArticleRepository articleRepository,
            StickerRepository stickerRepository,
            UserRepository userRepository,
            ArticleMapper articleMapper,
            NoteRepository noteRepository
    ) {
        this.articleRepository = articleRepository;
        this.stickerRepository = stickerRepository;
        this.userRepository = userRepository;
        this.articleMapper = articleMapper;
        this.noteRepository = noteRepository;
    }

    @Transactional
    public ArticleResponseTo createArticle(ArticleRequestTo dto) {

        User user = userRepository.findById(dto.userId())
                .orElseThrow(() -> new AssociationNotFoundException("User", dto.userId()));


        if (articleRepository.existsByTitle(dto.title())) {
            throw new DuplicateEntityException("title", dto.title());
        }

        Article article = articleMapper.toEntity(dto);
        article.setUser(user);

        if (dto.stickerIds() != null && !dto.stickerIds().isEmpty()) {
            List<Long> duplicateStickers = dto.stickerIds().stream()
                    .collect(Collectors.groupingBy(id -> id, Collectors.counting()))
                    .entrySet().stream()
                    .filter(entry -> entry.getValue() > 1)
                    .map(Map.Entry::getKey)
                    .collect(Collectors.toList());


            for (Long stickerId : dto.stickerIds()) {

                Sticker sticker = stickerRepository.findById(stickerId)
                        .orElseThrow(() -> new AssociationNotFoundException("Sticker", stickerId));
                article.addSticker(sticker);
            }
        }

        Article saved = articleRepository.save(article);
        return articleMapper.toResponse(saved);
    }

    @Transactional
    public ArticleResponseTo updateArticle(Long id, ArticleRequestTo dto) {
        Article article = articleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Article", "id", id));

        if (dto.title() != null && !dto.title().equals(article.getTitle())) {
            if (articleRepository.existsByTitle(dto.title())) {
                throw new DuplicateEntityException("title", dto.title());
            }
            article.setTitle(dto.title());
        }

        if (dto.content() != null) {
            article.setContent(dto.content());
        }

        if (dto.userId() != null && !dto.userId().equals(article.getUser().getId())) {
            User user = userRepository.findById(dto.userId())
                    .orElseThrow(() -> new AssociationNotFoundException("User", dto.userId()));
            article.setUser(user);
        }

        if (dto.stickerIds() != null) {

            List<Long> duplicateStickers = dto.stickerIds().stream()
                    .collect(Collectors.groupingBy(stickerId -> stickerId, Collectors.counting()))
                    .entrySet().stream()
                    .filter(entry -> entry.getValue() > 1)
                    .map(Map.Entry::getKey)
                    .collect(Collectors.toList());



            for (Sticker sticker : article.getStickers()) {
                sticker.getArticles().remove(article);
            }
            article.getStickers().clear();


            for (Long stickerId : dto.stickerIds()) {

                Sticker sticker = stickerRepository.findById(stickerId)
                        .orElseThrow(() -> new AssociationNotFoundException("Sticker", stickerId));
                article.addSticker(sticker);
            }
        }

        Article saved = articleRepository.save(article);
        return articleMapper.toResponse(saved);
    }

    @Transactional
    public void deleteArticle(Long articleId) {
        Article article = articleRepository.findById(articleId)
                .orElseThrow(() -> new ResourceNotFoundException("Article", "id", articleId));

        for (Sticker sticker : article.getStickers()) {
            sticker.getArticles().remove(article);
        }
        article.getStickers().clear();

        noteRepository.deleteByArticleId(articleId);
        articleRepository.deleteById(articleId);
    }

    public List<ArticleResponseTo> getAllArticles() {
        return articleRepository.findAll()
                .stream()
                .map(articleMapper::toResponse)
                .collect(Collectors.toList());
    }

    public ArticleResponseTo getArticleById(Long id) {
        Article article = articleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Article", "id", id));

        return articleMapper.toResponse(article);
    }

    public List<ArticleResponseTo> getArticlesPage(Pageable pageable) {
        return articleRepository.findAll(pageable)
                .map(articleMapper::toResponse)
                .getContent();
    }
}