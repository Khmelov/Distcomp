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
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class NewsService {
    private final NewsRepository newsRepository;
    private final CreatorRepository creatorRepository;
    private final MarkRepository markRepository;
    private final MarkService markService;  // Добавлено для автоматического создания меток
    private final NewsMapper mapper;

    @Transactional
    public NewsResponseTo create(NewsRequestTo request) {
        validateCreate(request);

        Creator creator = creatorRepository.findById(request.getCreatorId())
                .orElseThrow(() -> new NotFoundException("Creator not found with id: " + request.getCreatorId()));

        News entity = mapper.toEntity(request);
        entity.setCreator(creator);

        // Сначала сохраняем новость
        News saved = newsRepository.save(entity);

        // АВТОМАТИЧЕСКИ СОЗДАЁМ МЕТКИ С ID CREATOR
        String creatorId = String.valueOf(creator.getId());
        String[] colors = {"red", "green", "blue"};

        for (String color : colors) {
            String markName = color + creatorId;

            // Создаём метку, если её нет
            Mark mark = markRepository.findByName(markName)
                    .orElseGet(() -> {
                        Mark newMark = new Mark();
                        newMark.setName(markName);
                        return markRepository.save(newMark);
                    });

            // Привязываем к новости
            if (!saved.getMarks().contains(mark)) {
                saved.getMarks().add(mark);
            }
        }

        // Сохраняем новость с метками
        saved = newsRepository.save(saved);

        return mapper.toResponse(saved);
    }
    public List<NewsResponseTo> findAll() {
        return newsRepository.findAll().stream()
                .map(mapper::toResponse)
                .collect(Collectors.toList());
    }

    public NewsResponseTo findById(Long id) {
        return newsRepository.findById(id)
                .map(mapper::toResponse)
                .orElseThrow(() -> new NotFoundException("News not found with id: " + id));
    }

    public NewsResponseTo update(Long id, NewsRequestTo request) {
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
            return mapper.toResponse(updated);
        } catch (DataIntegrityViolationException e) {
            throw new DataIntegrityViolationException("duplicate key value violates unique constraint");
        }
    }

    public void delete(Long id) {
        if (!newsRepository.existsById(id)) {
            throw new NotFoundException("News not found with id: " + id);
        }
        newsRepository.deleteById(id);
    }

    @Transactional
    public NewsResponseTo addMarkToNews(Long newsId, String markName) {
        News news = newsRepository.findById(newsId)
                .orElseThrow(() -> new NotFoundException("News not found with id: " + newsId));

        // Автоматически создаёт метку, если её нет
        Mark mark = markService.getOrCreateMark(markName);

        if (!news.getMarks().contains(mark)) {
            news.getMarks().add(mark);
            news = newsRepository.save(news);
        }

        return mapper.toResponse(news);
    }

    @Transactional
    public void removeMarkFromNews(Long newsId, String markName) {
        News news = newsRepository.findById(newsId)
                .orElseThrow(() -> new NotFoundException("News not found with id: " + newsId));

        markRepository.findByName(markName).ifPresent(mark -> {
            news.getMarks().remove(mark);
            newsRepository.save(news);
        });
    }

    @Transactional
    public void removeAllMarksFromNews(Long newsId) {
        News news = newsRepository.findById(newsId)
                .orElseThrow(() -> new NotFoundException("News not found with id: " + newsId));

        news.getMarks().clear();
        newsRepository.save(news);
    }

    @Transactional
    public void deleteMarkCompletely(String markName) {
        markRepository.findByName(markName).ifPresent(mark -> {
            // Отвязываем от всех новостей
            for (News news : mark.getNews()) {
                news.getMarks().remove(mark);
                newsRepository.save(news);
            }
            // Удаляем саму метку
            markRepository.delete(mark);
            System.out.println("🗑️ Метка полностью удалена: " + markName);
        });
    }

    @Transactional
    public void deleteNewsAndOrphanMarks(Long newsId) {
        // Находим новость
        News news = newsRepository.findById(newsId)
                .orElseThrow(() -> new NotFoundException("News not found with id: " + newsId));

        // Сохраняем список меток перед удалением
        List<Mark> marksToCheck = new ArrayList<>(news.getMarks());

        // Удаляем новость (связи удалятся автоматически)
        newsRepository.delete(news);

        // Проверяем каждую метку - если она больше не привязана ни к одной новости, удаляем
        for (Mark mark : marksToCheck) {
            if (mark.getNews().isEmpty()) {
                markRepository.delete(mark);
                System.out.println("🗑️ Удалена метка-сирота: " + mark.getName());
            }
        }
    }

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