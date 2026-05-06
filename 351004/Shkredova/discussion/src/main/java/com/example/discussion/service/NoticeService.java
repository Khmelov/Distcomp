package com.example.discussion.service;

import com.example.discussion.dto.NoticeRequestTo;
import com.example.discussion.dto.NoticeResponseTo;
import com.example.discussion.exception.NotFoundException;
import com.example.discussion.exception.ValidationException;
import com.example.discussion.model.Notice;
import com.example.discussion.model.NoticeKey;
import com.example.discussion.repository.NoticeRepository;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

@Service
public class NoticeService {
    private final NoticeRepository noticeRepository;
    private final AtomicLong idGenerator = new AtomicLong(System.currentTimeMillis());

    public NoticeService(NoticeRepository noticeRepository) {
        this.noticeRepository = noticeRepository;
    }


    private NoticeResponseTo toResponse(Notice notice) {
        NoticeResponseTo response = new NoticeResponseTo();
        response.setId(notice.getId());
        response.setNewsId(notice.getNewsId());
        response.setContent(notice.getContent());
        response.setCreated(notice.getCreated());
        response.setModified(notice.getModified());
        response.setState(notice.getState() != null ? notice.getState() : "PENDING");
        return response;
    }

    private Notice toEntity(NoticeRequestTo request) {
        Notice notice = new Notice();
        notice.setCountry("by");
        notice.setNewsId(request.getNewsId());
        notice.setContent(request.getContent());
        return notice;
    }

    public List<NoticeResponseTo> findAll() {
        return noticeRepository.findAll().stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    public Optional<NoticeResponseTo> findById(Long id) {
        return noticeRepository.findAll().stream()
                .filter(n -> n.getId().equals(id))
                .findFirst()
                .map(this::toResponse);
    }

    public List<NoticeResponseTo> findAllByNewsId(Long newsId) {
        return noticeRepository.findByNewsId("by", newsId).stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    public Optional<NoticeResponseTo> findByNewsIdAndId(Long newsId, Long id) {
        return noticeRepository.findByNewsIdAndId("by", newsId, id)
                .map(this::toResponse);
    }

    public NoticeResponseTo create(NoticeRequestTo request) {
        validate(request);
        Notice entity = toEntity(request);
        Long newId = idGenerator.incrementAndGet();
        entity.setId(newId);
        entity.setCreated(LocalDateTime.now());
        entity.setModified(LocalDateTime.now());
        entity.setState("PENDING");
        Notice saved = noticeRepository.save(entity);
        return toResponse(saved);
    }

    public NoticeResponseTo update(NoticeRequestTo request) {
        if (request.getId() == null || request.getNewsId() == null) {
            throw new ValidationException("ID and newsId are required for update");
        }
        NoticeKey key = new NoticeKey("by", request.getNewsId(), request.getId());
        Notice existing = noticeRepository.findById(key)
                .orElseThrow(() -> new NotFoundException("Notice not found with id: " + request.getId()));
        existing.setContent(request.getContent());
        existing.setModified(LocalDateTime.now());
        Notice updated = noticeRepository.save(existing);
        return toResponse(updated);
    }

    public void delete(Long newsId, Long id) {
        NoticeKey key = new NoticeKey("by", newsId, id);
        if (!noticeRepository.existsById(key)) {
            throw new NotFoundException("Notice not found with newsId: " + newsId + " and id: " + id);
        }
        noticeRepository.deleteById(key);
    }

    private void validate(NoticeRequestTo request) {
        if (request.getContent() == null || request.getContent().trim().isEmpty()) {
            throw new ValidationException("Content is required");
        }
        if (request.getContent().length() < 2 || request.getContent().length() > 2048) {
            throw new ValidationException("Content must be between 2 and 2048 characters");
        }
        if (request.getNewsId() == null) {
            throw new ValidationException("News ID is required");
        }
    }
}