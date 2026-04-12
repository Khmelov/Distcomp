package com.example.task310.service;

import com.example.task310.dto.NoticeRequestTo;
import com.example.task310.dto.NoticeResponseTo;
import com.example.task310.exception.NotFoundException;
import com.example.task310.exception.ValidationException;
import com.example.task310.mapper.NoticeMapper;
import com.example.task310.model.News;
import com.example.task310.model.Notice;
import com.example.task310.repository.NewsRepository;
import com.example.task310.repository.NoticeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class NoticeService {
    private final NoticeRepository noticeRepository;
    private final NewsRepository newsRepository;
    private final NoticeMapper mapper;

    public NoticeResponseTo create(NoticeRequestTo request) {
        validateCreate(request);

        News news = newsRepository.findById(request.getNewsId())
                .orElseThrow(() -> new NotFoundException("News not found with id: " + request.getNewsId()));

        Notice entity = mapper.toEntity(request);
        entity.setNews(news);

        Notice saved = noticeRepository.save(entity);
        return mapper.toResponse(saved);
    }

    public List<NoticeResponseTo> findAll() {
        return noticeRepository.findAll().stream()
                .map(mapper::toResponse)
                .collect(Collectors.toList());
    }

    public NoticeResponseTo findById(Long id) {
        return noticeRepository.findById(id)
                .map(mapper::toResponse)
                .orElseThrow(() -> new NotFoundException("Notice not found with id: " + id));
    }

    public NoticeResponseTo update(Long id, NoticeRequestTo request) {
        Notice existing = noticeRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Notice not found with id: " + id));

        validateUpdate(request);

        existing.setContent(request.getContent());

        if (request.getNewsId() != null) {
            News news = newsRepository.findById(request.getNewsId())
                    .orElseThrow(() -> new NotFoundException("News not found with id: " + request.getNewsId()));
            existing.setNews(news);
        }

        Notice updated = noticeRepository.save(existing);
        return mapper.toResponse(updated);
    }

    public void delete(Long id) {
        if (!noticeRepository.existsById(id)) {
            throw new NotFoundException("Notice not found with id: " + id);
        }
        noticeRepository.deleteById(id);
    }

    private void validateCreate(NoticeRequestTo request) {
        if (request.getNewsId() == null) {
            throw new ValidationException("News ID is required");
        }
        if (request.getContent() == null || request.getContent().trim().isEmpty()) {
            throw new ValidationException("Content is required");
        }
        if (request.getContent().length() < 2 || request.getContent().length() > 2048) {
            throw new ValidationException("Content must be between 2 and 2048 characters");
        }
    }

    private void validateUpdate(NoticeRequestTo request) {
        if (request.getContent() != null && (request.getContent().length() < 2 || request.getContent().length() > 2048)) {
            throw new ValidationException("Content must be between 2 and 2048 characters");
        }
    }
}