package com.example.task310.service;

import com.example.task310.dto.MarkRequestTo;
import com.example.task310.dto.MarkResponseTo;
import com.example.task310.exception.NotFoundException;
import com.example.task310.exception.ValidationException;
import com.example.task310.mapper.MarkMapper;
import com.example.task310.model.Mark;
import com.example.task310.model.News;
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

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class MarkService {

    private final MarkRepository markRepository;
    private final NewsRepository newsRepository;
    private final MarkMapper mapper;

    // ==================== CREATE ====================
    @CachePut(value = "marks", key = "#result.id")
    public MarkResponseTo create(MarkRequestTo request) {
        log.info("Создание новой метки: {}", request.getName());
        validateCreate(request);

        try {
            Mark entity = mapper.toEntity(request);
            Mark saved = markRepository.save(entity);
            log.info("Метка создана с id = {}", saved.getId());
            return mapper.toResponse(saved);
        } catch (DataIntegrityViolationException e) {
            throw new DataIntegrityViolationException("duplicate key value violates unique constraint");
        }
    }

    // ==================== READ ====================
    public List<MarkResponseTo> findAll() {
        log.info("Запрос всех меток из БД");
        return markRepository.findAll().stream()
                .map(mapper::toResponse)
                .collect(Collectors.toList());
    }

    @Cacheable(value = "marks", key = "#id")
    public MarkResponseTo findById(Long id) {
        log.info("Кеш MISS: загрузка метки id = {} из БД", id);
        Mark entity = markRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Mark not found with id: " + id));
        return mapper.toResponse(entity);
    }

    // ==================== UPDATE ====================
    @CachePut(value = "marks", key = "#id")
    public MarkResponseTo update(Long id, MarkRequestTo request) {
        log.info("Обновление метки id = {}", id);
        Mark existing = markRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Mark not found with id: " + id));

        validateUpdate(request);
        existing.setName(request.getName());

        try {
            Mark updated = markRepository.save(existing);
            log.info("Метка id = {} обновлена, кеш обновлён", id);
            return mapper.toResponse(updated);
        } catch (DataIntegrityViolationException e) {
            throw new DataIntegrityViolationException("duplicate key value violates unique constraint");
        }
    }

    // ==================== DELETE ====================
    @CacheEvict(value = "marks", key = "#id")
    public void delete(Long id) {
        log.info("Удаление метки id = {}", id);
        Mark mark = markRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Mark not found with id: " + id));

        for (News news : mark.getNews()) {
            news.getMarks().remove(mark);
            newsRepository.save(news);
        }

        markRepository.deleteById(id);
        log.info("Метка id = {} удалена, кеш очищен", id);
    }

    // ==================== HELPER METHODS ====================
    public Optional<MarkResponseTo> findByName(String name) {
        return markRepository.findByName(name)
                .map(mapper::toResponse);
    }

    @CachePut(value = "marks", key = "#result.id")
    public Mark getOrCreateMark(String name) {
        log.info("Получение или создание метки: {}", name);
        return markRepository.findByName(name)
                .orElseGet(() -> {
                    Mark newMark = new Mark();
                    newMark.setName(name);
                    Mark saved = markRepository.save(newMark);
                    log.info("Автоматически создана новая метка: {}", name);
                    return saved;
                });
    }

    public void deleteByName(String name) {
        markRepository.findByName(name).ifPresent(mark -> {
            for (News news : mark.getNews()) {
                news.getMarks().remove(mark);
                newsRepository.save(news);
            }
            markRepository.delete(mark);
            log.info("Метка удалена по имени: {}", name);
        });
    }

    // ==================== ТЕСТОВЫЕ МЕТОДЫ ДЛЯ КОНТРОЛЛЕРА ====================
    @Transactional
    public void createMarkIfNotExists(String name) {
        if (!markRepository.existsByName(name)) {
            Mark mark = new Mark();
            mark.setName(name);
            markRepository.save(mark);
            log.info("Тестовая метка создана: {}", name);
        }
    }

    @Transactional
    public String createTestMarksForId(String id) {
        String[] colors = {"red", "green", "blue"};
        int created = 0;

        for (String color : colors) {
            String markName = color + id;
            if (!markRepository.existsByName(markName)) {
                Mark mark = new Mark();
                mark.setName(markName);
                markRepository.save(mark);
                created++;
            }
        }

        String result = "Создано меток: " + created + " для ID " + id;
        log.info(result);
        return result;
    }

    public boolean checkTestMarksForId(String id) {
        String[] colors = {"red", "green", "blue"};
        for (String color : colors) {
            if (!markRepository.existsByName(color + id)) {
                return false;
            }
        }
        return true;
    }

    @Transactional
    public String deleteTestMarksForId(String id) {
        String[] colors = {"red", "green", "blue"};
        final int[] deleted = {0};

        for (String color : colors) {
            String markName = color + id;
            if (markRepository.existsByName(markName)) {
                markRepository.findByName(markName).ifPresent(mark -> {
                    for (News news : mark.getNews()) {
                        news.getMarks().remove(mark);
                        newsRepository.save(news);
                    }
                    markRepository.delete(mark);
                    deleted[0]++;
                });
            }
        }

        String result = "Удалено меток: " + deleted[0] + " для ID " + id;
        log.info(result);
        return result;
    }

    // ==================== VALIDATION ====================
    private void validateCreate(MarkRequestTo request) {
        if (request.getName() == null || request.getName().trim().isEmpty()) {
            throw new ValidationException("Name is required");
        }
        if (request.getName().length() < 2 || request.getName().length() > 32) {
            throw new ValidationException("Mark name must be between 2 and 32 characters");
        }
    }

    private void validateUpdate(MarkRequestTo request) {
        if (request.getName() != null && (request.getName().length() < 2 || request.getName().length() > 32)) {
            throw new ValidationException("Mark name must be between 2 and 32 characters");
        }
    }
}