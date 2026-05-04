package com.example.task310.service;

import com.example.task310.dto.NewsRequestTo;
import com.example.task310.dto.NewsResponseTo;
import com.example.task310.exception.NotFoundException;
import com.example.task310.exception.ValidationException;
import com.example.task310.mapper.NewsMapper;
import com.example.task310.model.Creator;
import com.example.task310.model.Mark;
import com.example.task310.model.News;
import com.example.task310.repository.CreatorRepository;
import com.example.task310.repository.MarkRepository;
import com.example.task310.repository.NewsRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class NewsService {

    private final NewsRepository newsRepository;
    private final CreatorRepository creatorRepository;
    private final MarkRepository markRepository;
    private final MarkService markService;
    private final NewsMapper mapper;

    // ==================== CREATE ====================
    @CachePut(value = "news", key = "#result.id")
    public NewsResponseTo create(NewsRequestTo request) {
        log.info("Создание новой News, заголовок: {}", request.getTitle());
        validateCreate(request);

        Creator creator = creatorRepository.findById(request.getCreatorId())
                .orElseThrow(() -> new NotFoundException("Creator not found with id: " + request.getCreatorId()));

        News entity = mapper.toEntity(request);
        entity.setCreator(creator);

        try {
            News saved = newsRepository.save(entity);
            log.info("News создана с id = {}", saved.getId());
            return mapper.toResponse(saved);
        } catch (DataIntegrityViolationException e) {
            throw new DataIntegrityViolationException("duplicate key value violates unique constraint");
        }
    }

    // ==================== READ ====================
    public List<NewsResponseTo> findAll() {
        log.info("Запрос всех News из БД");
        return newsRepository.findAll().stream()
                .map(mapper::toResponse)
                .collect(Collectors.toList());
    }

    @Cacheable(value = "news", key = "#id")
    public NewsResponseTo findById(Long id) {
        log.info("Кеш MISS: загрузка News id = {} из БД", id);
        News entity = newsRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("News not found with id: " + id));
        return mapper.toResponse(entity);
    }

    // ==================== UPDATE ====================
    @CachePut(value = "news", key = "#id")
    public NewsResponseTo update(Long id, NewsRequestTo request) {
        log.info("Обновление News id = {}", id);
        News existing = newsRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("News not found with id: " + id));

        validateUpdate(request);

        existing.setTitle(request.getTitle());
        existing.setContent(request.getContent());

        if (request.getCreatorId() != null) {
            Creator creator = creatorRepository.findById(request.getCreatorId())
                    .orElseThrow(() -> new NotFoundException("Creator not found with id: " + request.getCreatorId()));
            existing.setCreator(creator);
        }

        try {
            News updated = newsRepository.save(existing);
            log.info("News id = {} обновлена, кеш обновлён", id);
            return mapper.toResponse(updated);
        } catch (DataIntegrityViolationException e) {
            throw new DataIntegrityViolationException("duplicate key value violates unique constraint");
        }
    }

    // ==================== DELETE ====================
    @CacheEvict(value = "news", key = "#id")
    public void delete(Long id) {
        log.info("Удаление News id = {}", id);
        if (!newsRepository.existsById(id)) {
            throw new NotFoundException("News not found with id: " + id);
        }
        newsRepository.deleteById(id);
        log.info("News id = {} удалена, кеш очищен", id);
    }

    // ==================== MARKS MANAGEMENT ====================
    public NewsResponseTo addMarkToNews(Long newsId, String markName) {
        News news = newsRepository.findById(newsId)
                .orElseThrow(() -> new NotFoundException("News not found with id: " + newsId));

        Mark mark = markService.getOrCreateMark(markName);

        if (!news.getMarks().contains(mark)) {
            news.getMarks().add(mark);
            news = newsRepository.save(news);
            log.info("Метка {} добавлена к новости {}", markName, newsId);
        }

        return mapper.toResponse(news);
    }

    public void removeMarkFromNews(Long newsId, String markName) {
        News news = newsRepository.findById(newsId)
                .orElseThrow(() -> new NotFoundException("News not found with id: " + newsId));

        markRepository.findByName(markName).ifPresent(mark -> {
            news.getMarks().remove(mark);
            newsRepository.save(news);
            log.info("Метка {} удалена из новости {}", markName, newsId);
        });
    }

    public void removeAllMarksFromNews(Long newsId) {
        News news = newsRepository.findById(newsId)
                .orElseThrow(() -> new NotFoundException("News not found with id: " + newsId));
        news.getMarks().clear();
        newsRepository.save(news);
        log.info("Все метки удалены из новости {}", newsId);
    }

    public void deleteMarkCompletely(String markName) {
        markRepository.findByName(markName).ifPresent(mark -> {
            for (News news : mark.getNews()) {
                news.getMarks().remove(mark);
                newsRepository.save(news);
            }
            markRepository.delete(mark);
            log.info("Метка полностью удалена: {}", markName);
        });
    }

    public void deleteNewsAndOrphanMarks(Long newsId) {
        News news = newsRepository.findById(newsId)
                .orElseThrow(() -> new NotFoundException("News not found with id: " + newsId));

        List<Mark> marksToCheck = new ArrayList<>(news.getMarks());
        newsRepository.delete(news);
        log.info("Новость id = {} удалена", newsId);

        for (Mark mark : marksToCheck) {
            if (mark.getNews().isEmpty()) {
                markRepository.delete(mark);
                log.info("Удалена метка-сирота: {}", mark.getName());
            }
        }
    }

    // ==================== VALIDATION ====================
    private void validateCreate(NewsRequestTo request) {
        if (request.getCreatorId() == null) {
            throw new ValidationException("Creator ID is required");
        }
        if (request.getTitle() == null || request.getTitle().trim().isEmpty()) {
            throw new ValidationException("Title is required");
        }
        if (request.getTitle().length() < 2 || request.getTitle().length() > 64) {
            throw new ValidationException("Title must be between 2 and 64 characters");
        }
        if (request.getContent() == null || request.getContent().trim().isEmpty()) {
            throw new ValidationException("Content is required");
        }
        if (request.getContent().length() < 4 || request.getContent().length() > 2048) {
            throw new ValidationException("Content must be between 4 and 2048 characters");
        }
    }

    private void validateUpdate(NewsRequestTo request) {
        if (request.getTitle() != null && (request.getTitle().length() < 2 || request.getTitle().length() > 64)) {
            throw new ValidationException("Title must be between 2 and 64 characters");
        }
        if (request.getContent() != null && (request.getContent().length() < 4 || request.getContent().length() > 2048)) {
            throw new ValidationException("Content must be between 4 and 2048 characters");
        }
    }
}