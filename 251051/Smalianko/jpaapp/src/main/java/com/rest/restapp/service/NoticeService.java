package com.rest.restapp.service;

import com.rest.restapp.dto.request.NoticeRequestToDto;
import com.rest.restapp.dto.response.NoticeResponseToDto;
import com.rest.restapp.exception.NotFoundException;
import com.rest.restapp.mapper.NoticeMapper;
import com.rest.restapp.repositry.IssueRepository;
import com.rest.restapp.repositry.NoticeRepository;
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

    NoticeRepository noticeRepository;
    IssueRepository issueRepository;
    NoticeMapper mapper;

    @Transactional
    public NoticeResponseToDto createNotice(NoticeRequestToDto requestTo) {
        validateNoticeRequest(requestTo);
        var issue = issueRepository.findById(requestTo.issueId())
                .orElseThrow(() -> new NotFoundException("Issue with id " + requestTo.issueId() + " not found"));

        var notice = mapper.toEntity(requestTo);
        notice.setIssue(issue);
        var savedNotice = noticeRepository.save(notice);
        return mapper.toResponseTo(savedNotice);
    }

    public NoticeResponseToDto getNoticeById(Long id) {
        var notice = noticeRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Notice with id " + id + " not found"));
        return mapper.toResponseTo(notice);
    }

    public List<NoticeResponseToDto> getAllNotices() {
        return noticeRepository.findAll().stream()
                .map(mapper::toResponseTo)
                .toList();
    }

    @Transactional
    public NoticeResponseToDto updateNotice(Long id, NoticeRequestToDto requestTo) {
        validateNoticeRequest(requestTo);
        var existingNotice = noticeRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Notice with id " + id + " not found"));

        var issue = issueRepository.findById(requestTo.issueId())
                .orElseThrow(() -> new NotFoundException("Issue with id " + requestTo.issueId() + " not found"));

        mapper.updateEntityFromDto(requestTo, existingNotice);
        existingNotice.setIssue(issue);
        var updatedNotice = noticeRepository.save(existingNotice);
        return mapper.toResponseTo(updatedNotice);
    }

    @Transactional
    public void deleteNotice(Long id) {
        if (!noticeRepository.existsById(id)) {
            throw new NotFoundException("Notice with id " + id + " not found");
        }
        noticeRepository.deleteById(id);
    }

    @Transactional(readOnly = true)
    public List<NoticeResponseToDto> getNoticesByIssueId(Long issueId) {
        return noticeRepository.findAll().stream()
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