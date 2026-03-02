package by.distcomp.task1.service;

import by.distcomp.task1.dto.ArticleRequestTo;
import by.distcomp.task1.dto.ArticleResponseTo;
import by.distcomp.task1.exception.ResourceNotFoundException;
import by.distcomp.task1.mapper.ArticleMapper;
import by.distcomp.task1.model.Article;
import by.distcomp.task1.model.Sticker;
import by.distcomp.task1.model.User;
import by.distcomp.task1.repository.ArticleRepository;
import by.distcomp.task1.repository.NoteRepository;
import by.distcomp.task1.repository.StickerRepository;
import by.distcomp.task1.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;
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
        this.noteRepository=noteRepository;
    }
    public ArticleResponseTo createArticle(ArticleRequestTo dto) {
        Article article = articleMapper.toEntity(dto);

        User user = userRepository.findById(dto.userId())
                .orElseThrow(() -> new NoSuchElementException("User not found"));
        article.setUser(user);

        if (dto.stickerIds() != null) {
            for (Long stickerId : dto.stickerIds()) {
                Sticker sticker = stickerRepository.findById(stickerId)
                        .orElseThrow(() -> new NoSuchElementException("Sticker not found: " + stickerId));
                article.addSticker(sticker);
            }
        }

        Article saved = articleRepository.save(article);

        return articleMapper.toResponse(saved);
    }
    public ArticleResponseTo updateArticle(Long id, ArticleRequestTo dto) {
        Article article = articleRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Article not found"));

        if (dto.title() != null) {
            article.setTitle(dto.title());
        }
        if (dto.content() != null) {
            article.setContent(dto.content());
        }

        if (dto.stickerIds() != null) {
            for (Sticker sticker : article.getStickers()) {
                sticker.getArticles().remove(article);
            }
            article.getStickers().clear();

            for (Long stickerId : dto.stickerIds()) {
                Sticker sticker = stickerRepository.findById(stickerId)
                        .orElseThrow(() -> new NoSuchElementException("Sticker not found: " + stickerId));
                article.addSticker(sticker);
            }
        }

        Article saved = articleRepository.save(article);
        return articleMapper.toResponse(saved);
    }
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
                .orElseThrow(() -> new NoSuchElementException("Article not found: " + id));

        return articleMapper.toResponse(article);
    }
}
