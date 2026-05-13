package by.bsuir.task340.publisher.service.impl;


import by.bsuir.task340.publisher.domain.Article;
import by.bsuir.task340.publisher.domain.Creator;
import by.bsuir.task340.publisher.dto.request.ArticleRequestTo;
import by.bsuir.task340.publisher.mapper.ArticleMapper;
import by.bsuir.task340.publisher.repository.ArticleRepository;
import by.bsuir.task340.publisher.repository.specification.ArticleSpecifications;
import by.bsuir.task340.publisher.service.ArticleService;
import by.bsuir.task340.publisher.dto.response.ArticleResponseTo;
import by.bsuir.task340.publisher.service.CreatorService;
import by.bsuir.task340.publisher.error.NotFoundException;
import by.bsuir.task340.publisher.error.ValidationException;
import by.bsuir.task340.publisher.error.AlreadyExistsException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class ArticleServiceImpl implements ArticleService {

    private final ArticleRepository repository;
    private final ArticleMapper mapper;
    private final CreatorService creatorService;
    private final PageableFactory pageableFactory;

    public ArticleServiceImpl(ArticleRepository repository,
                              ArticleMapper mapper,
                              CreatorService creatorService,
                              PageableFactory pageableFactory) {
        this.repository = repository;
        this.mapper = mapper;
        this.creatorService = creatorService;
        this.pageableFactory = pageableFactory;
    }

    @Override
    public ArticleResponseTo create(ArticleRequestTo request) {
        repository.findByTitle(request.getTitle())
                .ifPresent(article -> {
                    throw new AlreadyExistsException("Article title already exists");
                });

        Creator creator = creatorService.requireEntity(request.getCreatorId());

        Article article = new Article();
        article.setId(null);
        article.setCreator(creator);
        article.setTitle(request.getTitle());
        article.setContent(request.getContent());

        Article saved = repository.saveAndFlush(article);
        return mapper.toResponse(saved);
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
                .ifPresent(found -> {
                    throw new AlreadyExistsException("Article title already exists");
                });

        Creator creator = creatorService.requireEntity(request.getCreatorId());

        article.setCreator(creator);
        article.setTitle(request.getTitle());
        article.setContent(request.getContent());

        Article updated = repository.saveAndFlush(article);
        return mapper.toResponse(updated);
    }

    @Override
    @Transactional(readOnly = true)
    public ArticleResponseTo findById(Long id) {
        return mapper.toResponse(requireEntity(id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<ArticleResponseTo> findAll(Integer page, Integer size, String sort, String filter, Long creatorId) {
        var pageable = pageableFactory.create(page, size, sort, "id");
        return repository.findAll(ArticleSpecifications.byFilter(filter, creatorId), pageable)
                .getContent()
                .stream()
                .map(mapper::toResponse)
                .toList();
    }

    @Override
    public void delete(Long id) {
        Article article = requireEntity(id);
        repository.delete(article);
        repository.flush();
    }

    @Override
    @Transactional(readOnly = true)
    public Article requireEntity(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new NotFoundException("Article not found"));
    }
}

