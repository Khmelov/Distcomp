package com.task.rest.service;

import com.task.rest.client.DiscussionClient;
import com.task.rest.dto.NoticeRequestTo;
import com.task.rest.dto.NoticeResponseTo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class NoticeService {

    private final DiscussionClient discussionClient;
    private static final String DEFAULT_COUNTRY = "BY";

    public NoticeResponseTo create(NoticeRequestTo request) {
        log.info("Creating notice via discussion service");

        if (request.getId() == null) {
            request.setId(System.currentTimeMillis());
        }

        if (request.getCountry() == null || request.getCountry().isBlank()) {
            request.setCountry(DEFAULT_COUNTRY);
        }

        return discussionClient.createNotice(request);
    }

    public NoticeResponseTo getByIdOnly(Long id) {
        log.info("Getting notice by id only: {}", id);

        List<NoticeResponseTo> allNotices = discussionClient.getAllNotices();
        return allNotices.stream()
                .filter(notice -> notice.getId().equals(id))
                .findFirst()
                .orElse(null);
    }

    public NoticeResponseTo getById(String country, Long tweetId, Long id) {
        log.info("Getting notice: {}/{}/{}", country, tweetId, id);
        return discussionClient.getNoticeById(country, tweetId, id);
    }

    public List<NoticeResponseTo> getByTweetId(Long tweetId) {
        log.info("Getting notices by tweetId: {}", tweetId);
        return discussionClient.getNoticesByTweetId(tweetId, DEFAULT_COUNTRY);
    }

    public List<NoticeResponseTo> getAll() {
        log.info("Getting all notices");
        return discussionClient.getAllNotices();
    }

    public NoticeResponseTo updateByIdOnly(Long id, NoticeRequestTo request) {
        log.info("Updating notice by id only: {}", id);

        // Найдем существующий notice
        NoticeResponseTo existing = getByIdOnly(id);
        if (existing == null) {
            log.error("Notice not found with id: {}", id);
            return null;
        }

        // Если изменились ключевые поля (tweetId, country), нужно удалить и создать заново
        boolean keysChanged = !existing.getTweetId().equals(request.getTweetId()) ||
                (request.getCountry() != null && !existing.getCountry().equals(request.getCountry()));

        if (keysChanged) {
            log.info("Primary keys changed, recreating notice");
            // Удаляем старую запись
            discussionClient.deleteNotice(existing.getCountry(), existing.getTweetId(), existing.getId());

            // Создаем новую с теми же id
            request.setId(id);
            if (request.getCountry() == null || request.getCountry().isBlank()) {
                request.setCountry(DEFAULT_COUNTRY);
            }
            return discussionClient.createNotice(request);
        } else {
            // Обновляем только content
            request.setCountry(existing.getCountry());
            request.setTweetId(existing.getTweetId());
            return discussionClient.updateNotice(
                    existing.getCountry(),
                    existing.getTweetId(),
                    id,
                    request
            );
        }
    }

    public NoticeResponseTo update(String country, Long tweetId, Long id, NoticeRequestTo request) {
        log.info("Updating notice: {}/{}/{}", country, tweetId, id);
        return discussionClient.updateNotice(country, tweetId, id, request);
    }

    public void deleteByIdOnly(Long id) {
        log.info("Deleting notice by id only: {}", id);

        NoticeResponseTo existing = getByIdOnly(id);
        if (existing != null) {
            discussionClient.deleteNotice(existing.getCountry(), existing.getTweetId(), id);
        }
    }

    public void delete(String country, Long tweetId, Long id) {
        log.info("Deleting notice: {}/{}/{}", country, tweetId, id);
        discussionClient.deleteNotice(country, tweetId, id);
    }
}
