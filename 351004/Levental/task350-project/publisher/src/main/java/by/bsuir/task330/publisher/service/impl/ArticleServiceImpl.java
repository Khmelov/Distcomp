package by.bsuir.task330.publisher.service.impl;

import by.bsuir.task330.publisher.cache.RedisCacheService;
import by.bsuir.task330.publisher.domain.Article;
import by.bsuir.task330.publisher.domain.Creator;
import by.bsuir.task330.publisher.dto.request.ArticleRequestTo;
import by.bsuir.task330.publisher.dto.response.ArticleResponseTo;
import by.bsuir.task330.publisher.error.AlreadyExistsException;
import by.bsuir.task330.publisher.error.NotFoundException;
import by.bsuir.task330.publisher.error.ValidationException;
import by.bsuir.task330.publisher.mapper.ArticleMapper;
import by.bsuir.task330.publisher.repository.ArticleRepository;
import by.bsuir.task330.publisher.repository.specification.ArticleSpecifications;
import by.bsuir.task330.publisher.service.ArticleService;
import by.bsuir.task330.publisher.service.CreatorService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class ArticleServiceImpl implements ArticleService {

    private static final String CACHE_PREFIX = "article:";
    private static final String CACHE_LIST_PREFIX = "article:list:";

    private final ArticleRepository repository;
    private final ArticleMapper mapper;
    private final CreatorService creatorService;
    private final PageableFactory pageableFactory;
    private final RedisCacheService cache;
    private final ObjectMapper objectMapper;

    public ArticleServiceImpl(ArticleRepository repository,
                              ArticleMapper mapper,
                              CreatorService creatorService,
                              PageableFactory pageableFactory,
                              RedisCacheService cache,
                              ObjectMapper objectMapper) {
        this.repository = repository;
        this.mapper = mapper;
        this.creatorService = creatorService;
        this.pageableFactory = pageableFactory;
        this.cache = cache;
        this.objectMapper = objectMapper;
    }

    @Override
    public ArticleResponseTo create(ArticleRequestTo request) {
        repository.findByTitle(request.getTitle())
                .ifPresent(article -> { throw new AlreadyExistsException("Article title already exists"); });

        Creator creator = creatorService.requireEntity(request.getCreatorId());

        Article article = new Article();
        article.setId(null);
        article.setCreator(creator);
        article.setTitle(request.getTitle());
        article.setContent(request.getContent());

        Article saved = repository.saveAndFlush(article);
        ArticleResponseTo response = mapper.toResponse(saved);
        cache.save(CACHE_PREFIX + saved.getId(), response);
        cache.deleteByPrefix(CACHE_LIST_PREFIX);
        return response;
    }

    @Override
    public ArticleResponseTo update(ArticleRequestTo request) {
        if (request.getId() == null) {
            throw new ValidationException("id is required");
        }

        Article article = repository.findById(request.getId())
                .orElseThrow(() -> new NotFoundException("Article not found"));

        repository.findByTitle(request.getTitle())
                .filter(found -> !found.getId().equals(request.getId()))
                .ifPresent(found -> { throw new AlreadyExistsException("Article title already exists"); });

        Creator creator = creatorService.requireEntity(request.getCreatorId());

        article.setCreator(creator);
        article.setTitle(request.getTitle());
        article.setContent(request.getContent());

        Article updated = repository.saveAndFlush(article);
        ArticleResponseTo response = mapper.toResponse(updated);
        cache.save(CACHE_PREFIX + updated.getId(), response);
        cache.deleteByPrefix(CACHE_LIST_PREFIX);
        return response;
    }

    @Override
    @Transactional(readOnly = true)
    public ArticleResponseTo findById(Long id) {
        String key = CACHE_PREFIX + id;
        ArticleResponseTo cached = cache.get(key, ArticleResponseTo.class);
        if (cached != null) {
            return cached;
        }

        ArticleResponseTo response = mapper.toResponse(requireEntity(id));
        cache.save(key, response);
        return response;
    }

    @Override
    @Transactional(readOnly = true)
    public List<ArticleResponseTo> findAll(Integer page, Integer size, String sort, String filter, Long creatorId) {
        String key = CACHE_LIST_PREFIX + page + ":" + size + ":" + sort + ":" + filter + ":" + creatorId;
        Object cached = cache.get(key);
        if (cached instanceof List<?> list) {
            return list.stream()
                    .map(item -> item instanceof ArticleResponseTo dto ? dto : objectMapper.convertValue(item, ArticleResponseTo.class))
                    .toList();
        }

        var pageable = pageableFactory.create(page, size, sort, "id");
        List<ArticleResponseTo> response = repository.findAll(ArticleSpecifications.byFilter(filter, creatorId), pageable)
                .getContent()
                .stream()
                .map(mapper::toResponse)
                .toList();
        cache.save(key, response);
        return response;
    }

    @Override
    public void delete(Long id) {
        Article article = requireEntity(id);
        repository.delete(article);
        repository.flush();
        cache.delete(CACHE_PREFIX + id);
        cache.deleteByPrefix(CACHE_LIST_PREFIX);
    }

    @Override
    @Transactional(readOnly = true)
    public Article requireEntity(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new NotFoundException("Article not found"));
    }
}
