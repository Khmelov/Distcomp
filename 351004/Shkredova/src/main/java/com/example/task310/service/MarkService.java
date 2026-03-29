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
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.Optional;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class MarkService {
    private final MarkRepository markRepository;
    private final NewsRepository newsRepository;
    private final MarkMapper mapper;

    public MarkResponseTo create(MarkRequestTo request) {
        validateCreate(request);

        try {
            Mark entity = mapper.toEntity(request);
            Mark saved = markRepository.save(entity);
            return mapper.toResponse(saved);
        } catch (DataIntegrityViolationException e) {
            throw new DataIntegrityViolationException("duplicate key value violates unique constraint");
        }
    }

    public List<MarkResponseTo> findAll() {
        return markRepository.findAll().stream()
                .map(mapper::toResponse)
                .collect(Collectors.toList());
    }

    public MarkResponseTo findById(Long id) {
        return markRepository.findById(id)
                .map(mapper::toResponse)
                .orElseThrow(() -> new NotFoundException("Mark not found with id: " + id));
    }

    public MarkResponseTo update(Long id, MarkRequestTo request) {
        Mark existing = markRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Mark not found with id: " + id));

        validateUpdate(request);

        existing.setName(request.getName());

        try {
            Mark updated = markRepository.save(existing);
            return mapper.toResponse(updated);
        } catch (DataIntegrityViolationException e) {
            throw new DataIntegrityViolationException("duplicate key value violates unique constraint");
        }
    }

    public void delete(Long id) {
        if (!markRepository.existsById(id)) {
            throw new NotFoundException("Mark not found with id: " + id);
        }

        Mark mark = markRepository.findById(id).get();
        // Отвязываем от всех новостей
        for (News news : mark.getNews()) {
            news.getMarks().remove(mark);
            newsRepository.save(news);
        }

        markRepository.deleteById(id);
    }

    public Optional<MarkResponseTo> findByName(String name) {
        return markRepository.findByName(name)
                .map(mapper::toResponse);
    }

    @Transactional
    public void deleteByName(String name) {
        markRepository.findByName(name).ifPresent(mark -> {
            // Отвязываем от всех новостей
            for (News news : mark.getNews()) {
                news.getMarks().remove(mark);
                newsRepository.save(news);
            }
            markRepository.delete(mark);
            System.out.println("🗑️ Удалена метка: " + name);
        });
    }

    public boolean existsByName(String name) {
        return markRepository.findByName(name).isPresent();
    }

    @Transactional
    public Mark getOrCreateMark(String name) {
        return markRepository.findByName(name)
                .orElseGet(() -> {
                    Mark mark = new Mark();
                    mark.setName(name);
                    return markRepository.save(mark);
                });


    }


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

    public MarkResponseTo toResponse(Mark mark) {
        return mapper.toResponse(mark);
    }

    @Transactional
    public void ensureMarkExists(String name) {
        if (!markRepository.existsByName(name)) {
            Mark mark = new Mark();
            mark.setName(name);
            markRepository.save(mark);
        }
    }




}