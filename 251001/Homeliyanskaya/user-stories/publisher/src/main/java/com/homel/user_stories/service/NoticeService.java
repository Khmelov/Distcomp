package com.homel.user_stories.service;

import com.homel.user_stories.dto.NoticeRequestTo;
import com.homel.user_stories.dto.NoticeResponseTo;
import com.homel.user_stories.exception.EntityNotFoundException;
import com.homel.user_stories.mapper.NoticeMapper;
import com.homel.user_stories.model.Notice;
import com.homel.user_stories.repository.NoticeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Component
public class NoticeService {

    private final RestTemplate restTemplate;
    private final RedisCacheService cacheService;

    private static final String NOTICE_CACHE_PREFIX = "notice::";

    @Autowired
    public NoticeService(RestTemplate restTemplate, RedisCacheService cacheService) {
        this.restTemplate = restTemplate;
        this.cacheService = cacheService;
    }

    private final String BASE_URL = "http://localhost:24130/api/v1.0/notices";

    public NoticeResponseTo createNotice(NoticeRequestTo noticeRequest) {
        String url = BASE_URL;
        NoticeResponseTo createdNotice = restTemplate.postForObject(url, noticeRequest, NoticeResponseTo.class);

        cacheService.put(NOTICE_CACHE_PREFIX + createdNotice.getId(), createdNotice);
        return createdNotice;
    }

    public NoticeResponseTo getNotice(Long id) {
        String cacheKey = NOTICE_CACHE_PREFIX + id;
        NoticeResponseTo cached = cacheService.get(cacheKey, NoticeResponseTo.class);
        if (cached != null) {
            return cached;
        }

        String url = BASE_URL + "/" + id;
        NoticeResponseTo notice = restTemplate.getForObject(url, NoticeResponseTo.class);

        if (notice != null) {
            cacheService.put(cacheKey, notice);
        } else {
            throw new EntityNotFoundException("Notice not found");
        }

        return notice;
    }

    public List<NoticeResponseTo> getAllNotices() {
        String url = BASE_URL;
        NoticeResponseTo[] noticesArray = restTemplate.getForObject(url, NoticeResponseTo[].class);
        List<NoticeResponseTo> notices = noticesArray != null ? Arrays.asList(noticesArray) : Collections.emptyList();

        if (!notices.isEmpty()) {
            for (NoticeResponseTo notice : notices) {
                cacheService.put(NOTICE_CACHE_PREFIX + notice.getId(), notice);
            }
        }

        return notices;
    }

    public void deleteNotice(Long id) {
        String url = BASE_URL + "/" + id;
        restTemplate.delete(url);

        cacheService.evict(NOTICE_CACHE_PREFIX + id);
    }

    public NoticeResponseTo updateNotice(NoticeRequestTo noticeRequest) {
        String url = BASE_URL;
        ResponseEntity<NoticeResponseTo> responseEntity = restTemplate.exchange(
                url,
                HttpMethod.PUT,
                new HttpEntity<>(noticeRequest),
                NoticeResponseTo.class
        );

        NoticeResponseTo updatedNotice = responseEntity.getBody();

        if (updatedNotice != null) {
            cacheService.put(NOTICE_CACHE_PREFIX + updatedNotice.getId(), updatedNotice);
        }

        return updatedNotice;
    }
}
