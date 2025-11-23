package com.rest.restapp.service;

import com.rest.restapp.dto.request.NoticeRequestToDto;
import com.rest.restapp.dto.response.NoticeResponseToDto;
import com.rest.restapp.exception.NotFoundException;
import com.rest.restapp.mapper.NoticeMapper;
import com.rest.restapp.repositry.InMemoryRepository;
import jakarta.validation.ValidationException;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class NoticeService {

    InMemoryRepository repository;
    NoticeMapper mapper;

    @Transactional
    public NoticeResponseToDto createNotice(NoticeRequestToDto requestTo) {
        validateNoticeRequest(requestTo);
        var issue = repository.findIssueById(requestTo.issueId())
                .orElseThrow(() -> new NotFoundException("Issue with id " + requestTo.issueId() + " not found"));

        var notice = mapper.toEntity(requestTo);
        notice.setIssue(issue);
        var savedNotice = repository.saveNotice(notice);
        return mapper.toResponseTo(savedNotice);
    }

    public NoticeResponseToDto getNoticeById(Long id) {
        var notice = repository.findNoticeById(id)
                .orElseThrow(() -> new NotFoundException("Notice with id " + id + " not found"));
        return mapper.toResponseTo(notice);
    }

    public List<NoticeResponseToDto> getAllNotices() {
        return repository.findAllNotices().stream()
                .map(mapper::toResponseTo)
                .toList();
    }

    @Transactional
    public NoticeResponseToDto updateNotice(Long id, NoticeRequestToDto requestTo) {
        validateNoticeRequest(requestTo);
        var existingNotice = repository.findNoticeById(id)
                .orElseThrow(() -> new NotFoundException("Notice with id " + id + " not found"));

        var issue = repository.findIssueById(requestTo.issueId())
                .orElseThrow(() -> new NotFoundException("Issue with id " + requestTo.issueId() + " not found"));

        mapper.updateEntityFromDto(requestTo, existingNotice);
        existingNotice.setIssue(issue);
        var updatedNotice = repository.saveNotice(existingNotice);
        return mapper.toResponseTo(updatedNotice);
    }

    @Transactional
    public void deleteNotice(Long id) {
        if (!repository.existsNoticeById(id)) {
            throw new NotFoundException("Notice with id " + id + " not found");
        }
        repository.deleteNoticeById(id);
    }

    @Transactional(readOnly = true)
    public List<NoticeResponseToDto> getNoticesByIssueId(Long issueId) {
        return repository.findAllNotices().stream()
                .filter(notice -> notice.getIssue().getId().equals(issueId))
                .map(mapper::toResponseTo)
                .toList();
    }

    private void validateNoticeRequest(NoticeRequestToDto requestTo) {
        if (requestTo == null) {
            throw new ValidationException("Notice request cannot be null");
        }
        if (requestTo.issueId() == null) {
            throw new ValidationException("Issue ID is required");
        }
        if (requestTo.content() == null || requestTo.content().trim().isEmpty()) {
            throw new ValidationException("Content is required");
        }
    }
}