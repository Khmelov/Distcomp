package com.adashkevich.redis.lab.service;

import com.adashkevich.redis.lab.dto.request.EditorRequestTo;
import com.adashkevich.redis.lab.dto.response.EditorResponseTo;
import com.adashkevich.redis.lab.exception.ForbiddenException;
import com.adashkevich.redis.lab.exception.NotFoundException;
import com.adashkevich.redis.lab.repository.EditorRepository;
import com.adashkevich.redis.lab.repository.NewsRepository;
import com.adashkevich.redis.lab.mapper.EditorMapper;
import com.adashkevich.redis.lab.model.Editor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class EditorService {
    private final EditorRepository repo;
    private final NewsRepository newsRepo;
    private final EditorMapper mapper;
    private final NewsService newsService;

    public EditorService(
            EditorRepository repo,
            NewsRepository newsRepo,
            EditorMapper mapper,
            NewsService newsService
    ) {
        this.repo = repo;
        this.newsRepo = newsRepo;
        this.mapper = mapper;
        this.newsService = newsService;
    }

    @CacheEvict(cacheNames = {"editors"}, allEntries = true)
    @Transactional
    public EditorResponseTo create(EditorRequestTo dto) {
        ensureLoginUnique(dto.login, null);
        Editor saved = repo.save(mapper.toEntity(dto));
        return mapper.toResponse(saved);
    }

    @Cacheable(cacheNames = "editors")
    public List<EditorResponseTo> getAll() {
        return repo.findAll(Sort.by("id")).stream().map(mapper::toResponse).toList();
    }

    @Cacheable(cacheNames = "editor", key = "#id")
    public EditorResponseTo getById(Long id) {
        return mapper.toResponse(findExisting(id));
    }

    @CacheEvict(cacheNames = {"editors", "editor", "news", "newsItem"}, allEntries = true)
    @Transactional
    public EditorResponseTo update(Long id, EditorRequestTo dto) {
        Editor existing = findExisting(id);
        ensureLoginUnique(dto.login, id);
        existing.setLogin(dto.login);
        existing.setPassword(dto.password);
        existing.setFirstname(dto.firstname);
        existing.setLastname(dto.lastname);
        return mapper.toResponse(repo.save(existing));
    }

    @CacheEvict(cacheNames = {"editors", "editor", "news", "newsItem", "markers", "marker", "messages", "message", "messagesByNews"}, allEntries = true)
    @Transactional
    public void delete(Long id) {
        Editor editor = findExisting(id);

        List<Long> newsIds = newsRepo.findAll().stream()
                .filter(news -> id.equals(news.getEditorId()))
                .map(news -> news.getId())
                .toList();

        for (Long newsId : newsIds) {
            newsService.delete(newsId);
        }

        repo.delete(editor);
    }

    private Editor findExisting(Long id) {
        return repo.findById(id)
                .orElseThrow(() -> new NotFoundException("Editor not found", "40410"));
    }

    private void ensureLoginUnique(String login, Long selfId) {
        boolean exists = selfId == null
                ? repo.existsByLoginIgnoreCase(login)
                : repo.existsByLoginIgnoreCaseAndIdNot(login, selfId);

        if (exists) {
            throw new ForbiddenException("Editor login must be unique", "40310");
        }
    }
}