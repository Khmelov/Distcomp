package com.example.discussion.service;

import com.example.discussion.dto.NoticeMessage;
import com.example.discussion.model.Notice;
import com.example.discussion.repository.NoticeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.concurrent.atomic.AtomicLong;

@Slf4j
@Service
@RequiredArgsConstructor
public class ModerationService {
    private final NoticeRepository noticeRepository;
    private final AtomicLong idGenerator = new AtomicLong(System.currentTimeMillis());

    private static final String[] STOP_WORDS = {"spam", "badword", "offensive"};

    public NoticeMessage moderateAndSave(NoticeMessage request) {
        boolean isValid = isContentValid(request.getContent());
        String finalState = isValid ? "APPROVED" : "DECLINED";

        long newId = idGenerator.incrementAndGet();
        Notice notice = new Notice();
        notice.setCountry("by");
        notice.setNewsId(request.getNewsId());
        notice.setId(newId);
        notice.setContent(request.getContent());
        notice.setState(finalState);
        notice.setCreated(LocalDateTime.now());
        notice.setModified(LocalDateTime.now());

        Notice saved = noticeRepository.save(notice);

        log.info("Notice saved with id: {}, state: {}", saved.getId(), finalState);

        NoticeMessage response = new NoticeMessage();
        response.setId(saved.getId());
        response.setNewsId(saved.getNewsId());
        response.setContent(saved.getContent());
        response.setState(finalState);
        response.setCorrelationId(request.getCorrelationId());
        return response;
    }

    private boolean isContentValid(String content) {
        if (content == null) return false;
        String lower = content.toLowerCase();
        for (String word : STOP_WORDS) {
            if (lower.contains(word)) return false;
        }
        return true;
    }
}