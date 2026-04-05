package by.distcomp.app.service;

import by.distcomp.app.model.Article;
import by.distcomp.app.dto.ArticleRequestTo;
import by.distcomp.app.dto.ArticleResponseTo;
import by.distcomp.app.dto.NoteResponseTo;
import by.distcomp.app.exception.AssociationNotFoundException;
import by.distcomp.app.exception.DuplicateEntityException;
import by.distcomp.app.exception.ResourceNotFoundException;
import by.distcomp.app.mapper.ArticleMapper;
import by.distcomp.app.model.Sticker;
import by.distcomp.app.model.User;
import by.distcomp.app.repository.ArticleRepository;
import by.distcomp.app.repository.StickerRepository;
import by.distcomp.app.repository.UserRepository;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestClient;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class ArticleService {

    private final ArticleRepository articleRepository;
    private final StickerRepository stickerRepository;
    private final UserRepository userRepository;
    private final ArticleMapper articleMapper;
    private final RestClient discussionClient;

    public ArticleService(
            ArticleRepository articleRepository,
            StickerRepository stickerRepository,
            UserRepository userRepository,
            ArticleMapper articleMapper,
            RestClient discussionClient
    ) {
        this.articleRepository = articleRepository;
        this.stickerRepository = stickerRepository;
        this.userRepository = userRepository;
        this.articleMapper = articleMapper;
        this.discussionClient = discussionClient;
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

        if (dto.stickers() != null) {
            processStickers(article, dto.stickers());
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

        if (dto.stickers() != null) {
            article.clearStickers();
            processStickers(article, dto.stickers());
        }

        Article saved = articleRepository.save(article);
        return articleMapper.toResponse(saved);
    }

    private void processStickers(Article article, List<String> stickerNames) {
        for (String name : stickerNames) {
            Sticker sticker = stickerRepository.findByName(name)
                    .orElseGet(() -> {
                        Sticker newSticker = new Sticker();
                        newSticker.setName(name);
                        return stickerRepository.save(newSticker);
                    });
            article.addSticker(sticker);
        }
    }

    @Transactional
    public void deleteArticle(Long articleId) {
        Article article = articleRepository.findById(articleId)
                .orElseThrow(() -> new ResourceNotFoundException("Article", "id", articleId));

        Set<Sticker> stickers = new HashSet<>(article.getStickers());
        article.clearStickers();

        discussionClient.delete()
                .uri("/article/{articleId}", articleId)
                .retrieve()
                .toBodilessEntity();

        articleRepository.delete(article);

        for (Sticker sticker : stickers) {
            if (sticker.getArticles().isEmpty()) {
                stickerRepository.delete(sticker);
            }
        }
    }

    public ArticleResponseTo getArticleById(Long id) {
        Article article = articleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Article", "id", id));

        ArticleResponseTo base = articleMapper.toResponse(article);

        List<NoteResponseTo> notes;
        try {
            notes = discussionClient.get()
                    .uri("/article/{articleId}", id)
                    .retrieve()
                    .body(new ParameterizedTypeReference<List<NoteResponseTo>>() {});
        } catch (Exception e) {
            notes = List.of();
        }

        return base.toBuilder().notes(notes).build();
    }

    public List<ArticleResponseTo> getArticlesPage(Pageable pageable) {
        return articleRepository.findAll(pageable)
                .map(articleMapper::toResponse)
                .getContent();
    }
}