package com.rest.restapp.service;

import com.rest.restapp.client.DiscussionClient;
import com.rest.restapp.dto.request.NoticeRequestToDto;
import com.rest.restapp.dto.response.NoticeResponseToDto;
import com.rest.restapp.exception.ValidationException;
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

    DiscussionClient discussionClient;

    public NoticeResponseToDto createNotice(NoticeRequestToDto requestTo) {
        validateNoticeRequest(requestTo);
        return discussionClient.createNotice(requestTo);
    }

    public NoticeResponseToDto getNoticeById(Long id) {
        return discussionClient.getById(id);
    }

    public List<NoticeResponseToDto> getAllNotices() {
        return discussionClient.getAll();
    }

    @Transactional
    public NoticeResponseToDto updateNotice(Long id, NoticeRequestToDto requestTo) {
        validateNoticeRequest(requestTo);
        return discussionClient.update(id, requestTo);
    }

    @Transactional
    public void deleteNotice(Long id) {
        discussionClient.deleteNotice(id);
    }

    @Transactional(readOnly = true)
    public List<NoticeResponseToDto> getNoticesByIssueId(Long issueId) {
        return discussionClient.getNoticesByIssueId(issueId);
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
