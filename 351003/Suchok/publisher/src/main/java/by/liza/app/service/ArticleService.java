package by.liza.app.service;

import by.liza.app.dto.request.ArticleRequestTo;
import by.liza.app.dto.response.ArticleResponseTo;
import by.liza.app.exception.DuplicateEntityException;
import by.liza.app.exception.EntityNotFoundException;
import by.liza.app.mapper.ArticleMapper;
import by.liza.app.model.Article;
import by.liza.app.model.Mark;
import by.liza.app.model.Writer;
import by.liza.app.repository.ArticleRepository;
import by.liza.app.repository.MarkRepository;
import by.liza.app.repository.WriterRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ArticleService {

    private final ArticleRepository articleRepository;
    private final WriterRepository  writerRepository;
    private final MarkRepository    markRepository;
    private final ArticleMapper     articleMapper;

    @Transactional
    @Caching(
            put  = @CachePut(value = "articles", key = "#result.id"),
            evict = @CacheEvict(value = "articles_all", allEntries = true)
    )
    public ArticleResponseTo create(ArticleRequestTo requestTo) {
        Writer writer = writerRepository.findById(requestTo.getWriterId())
                .orElseThrow(() -> new EntityNotFoundException(
                        "Writer with id " + requestTo.getWriterId() + " not found", 40402));

        if (articleRepository.existsByTitle(requestTo.getTitle())) {
            throw new DuplicateEntityException(
                    "Article with title '" + requestTo.getTitle() + "' already exists", 40302);
        }

        Article article = articleMapper.toEntity(requestTo);
        article.setWriter(writer);
        article.setCreated(LocalDateTime.now());
        article.setModified(LocalDateTime.now());
        article.setMarks(resolveMarks(requestTo.getMarkIds(), requestTo.getMarks()));

        return articleMapper.toResponse(articleRepository.save(article));
    }

    @Cacheable(value = "articles", key = "#id")
    public ArticleResponseTo getById(Long id) {
        return articleMapper.toResponse(findById(id));
    }

    @Cacheable(value = "articles_all")
    public List<ArticleResponseTo> getAll() {
        return articleMapper.toResponseList(articleRepository.findAll());
    }

    @Transactional
    @Caching(
            put  = @CachePut(value = "articles", key = "#result.id"),
            evict = @CacheEvict(value = "articles_all", allEntries = true)
    )
    public ArticleResponseTo update(ArticleRequestTo requestTo) {
        if (requestTo.getId() == null) {
            throw new EntityNotFoundException("Article id must be provided for update", 40002);
        }
        Article existing = findById(requestTo.getId());

        Writer writer = writerRepository.findById(requestTo.getWriterId())
                .orElseThrow(() -> new EntityNotFoundException(
                        "Writer with id " + requestTo.getWriterId() + " not found", 40402));

        if (articleRepository.existsByTitleAndIdNot(requestTo.getTitle(), requestTo.getId())) {
            throw new DuplicateEntityException(
                    "Article with title '" + requestTo.getTitle() + "' already exists", 40302);
        }

        existing.setWriter(writer);
        existing.setTitle(requestTo.getTitle());
        existing.setContent(requestTo.getContent());
        existing.setModified(LocalDateTime.now());
        existing.setMarks(resolveMarks(requestTo.getMarkIds(), requestTo.getMarks()));

        return articleMapper.toResponse(articleRepository.save(existing));
    }

    @Transactional
    @Caching(evict = {
            @CacheEvict(value = "articles",     key = "#id"),
            @CacheEvict(value = "articles_all", allEntries = true)
    })
    public void delete(Long id) {
        Article article = articleRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Article with id " + id + " not found", 40402));

        List<Long> markIds = article.getMarks().stream()
                .map(Mark::getId)
                .toList();

        article.getMarks().clear();
        articleRepository.saveAndFlush(article);

        for (Long markId : markIds) {
            List<Article> stillUsed = articleRepository.findByMarkId(markId);
            if (stillUsed.isEmpty()) {
                markRepository.deleteById(markId);
            }
        }

        articleRepository.delete(article);
    }

    private List<Mark> resolveMarks(List<Long> markIds, List<String> markNames) {
        List<Mark> result = new ArrayList<>();

        if (markIds != null && !markIds.isEmpty()) {
            result.addAll(markRepository.findAllById(markIds));
        }

        if (markNames != null && !markNames.isEmpty()) {
            for (String name : markNames) {
                Mark mark = markRepository.findByName(name)
                        .orElseGet(() -> markRepository.save(
                                Mark.builder().name(name).build()
                        ));
                if (!result.contains(mark)) {
                    result.add(mark);
                }
            }
        }

        return result;
    }

    private Article findById(Long id) {
        return articleRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Article with id " + id + " not found", 40402));
    }
}