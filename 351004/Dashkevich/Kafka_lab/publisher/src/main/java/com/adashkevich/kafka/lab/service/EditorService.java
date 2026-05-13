package com.adashkevich.kafka.lab.service;

import com.adashkevich.kafka.lab.dto.request.EditorRequestTo;
import com.adashkevich.kafka.lab.dto.response.EditorResponseTo;
import com.adashkevich.kafka.lab.exception.ForbiddenException;
import com.adashkevich.kafka.lab.exception.NotFoundException;
import com.adashkevich.kafka.lab.repository.EditorRepository;
import com.adashkevich.kafka.lab.repository.NewsRepository;
import com.adashkevich.kafka.lab.mapper.EditorMapper;
import com.adashkevich.kafka.lab.model.Editor;
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

    @Transactional
    public EditorResponseTo create(EditorRequestTo dto) {
        ensureLoginUnique(dto.login, null);
        Editor saved = repo.save(mapper.toEntity(dto));
        return mapper.toResponse(saved);
    }

    public List<EditorResponseTo> getAll() {
        return repo.findAll(Sort.by("id")).stream().map(mapper::toResponse).toList();
    }

    public EditorResponseTo getById(Long id) {
        return mapper.toResponse(findExisting(id));
    }

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