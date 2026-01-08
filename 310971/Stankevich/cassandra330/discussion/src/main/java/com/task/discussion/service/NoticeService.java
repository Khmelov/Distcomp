package com.task.discussion.service;

import com.task.discussion.dto.NoticeRequestTo;
import com.task.discussion.dto.NoticeResponseTo;
import com.task.discussion.exception.ResourceNotFoundException;
import com.task.discussion.mapper.NoticeMapper;
import com.task.discussion.model.Notice;
import com.task.discussion.model.NoticePrimaryKey;
import com.task.discussion.repository.NoticeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class NoticeService {

    private final NoticeRepository noticeRepository;
    private final NoticeMapper noticeMapper;

    public NoticeResponseTo create(NoticeRequestTo request) {
        log.info("Creating notice for country: {}, tweetId: {}", request.getCountry(), request.getTweetId());

        Notice notice = new Notice();
        notice.setCountry(request.getCountry());
        notice.setTweetId(request.getTweetId());
        notice.setId(request.getId() != null ? request.getId() : System.currentTimeMillis());
        notice.setContent(request.getContent());

        Notice saved = noticeRepository.save(notice);
        log.info("Notice created successfully with id: {}", saved.getId());

        return noticeMapper.toResponseTo(saved);
    }

    public List<NoticeResponseTo> getAll() {
        log.info("Retrieving all notices");
        return noticeRepository.findAll().stream()
                .map(noticeMapper::toResponseTo)
                .collect(Collectors.toList());
    }

    public NoticeResponseTo getByIdOnly(Long id) {
        log.info("Retrieving notice by id only: {}", id);

        // Ищем среди всех notices
        return noticeRepository.findAll().stream()
                .filter(notice -> notice.getId().equals(id))
                .findFirst()
                .map(noticeMapper::toResponseTo)
                .orElse(null);
    }

    public NoticeResponseTo getById(String country, Long tweetId, Long id) {
        log.info("Retrieving notice by country: {}, tweetId: {}, id: {}", country, tweetId, id);

        return noticeRepository.findByCountryAndTweetIdAndId(country, tweetId, id)
                .map(noticeMapper::toResponseTo)
                .orElseThrow(() -> {
                    log.error("Notice not found: {}/{}/{}", country, tweetId, id);
                    return new ResourceNotFoundException(
                            String.format("Notice not found with country: %s, tweetId: %d, id: %d", country, tweetId, id)
                    );
                });
    }

    public List<NoticeResponseTo> getByCountryAndTweetId(String country, Long tweetId) {
        log.info("Retrieving notices by country: {}, tweetId: {}", country, tweetId);

        return noticeRepository.findByCountryAndTweetId(country, tweetId).stream()
                .map(noticeMapper::toResponseTo)
                .collect(Collectors.toList());
    }

    public List<NoticeResponseTo> getByTweetId(Long tweetId, String country) {
        log.info("Retrieving notices by tweetId: {} and country: {}", tweetId, country);

        return noticeRepository.findByCountryAndTweetId(country, tweetId).stream()
                .map(noticeMapper::toResponseTo)
                .collect(Collectors.toList());
    }

    public NoticeResponseTo updateByIdOnly(Long id, NoticeRequestTo request) {
        log.info("Updating notice by id only: {}", id);

        // Найдем существующий notice
        Notice existing = noticeRepository.findAll().stream()
                .filter(notice -> notice.getId().equals(id))
                .findFirst()
                .orElseThrow(() -> {
                    log.error("Notice not found for update: {}", id);
                    return new ResourceNotFoundException(
                            String.format("Notice not found with id: %d", id)
                    );
                });

        // В Cassandra нельзя обновить primary key, поэтому если изменились ключи - удаляем и создаем заново
        boolean keysChanged = !existing.getTweetId().equals(request.getTweetId()) ||
                (request.getCountry() != null && !existing.getCountry().equals(request.getCountry()));

        if (keysChanged) {
            log.info("Primary keys changed, recreating notice");
            noticeRepository.delete(existing);

            Notice newNotice = new Notice();
            newNotice.setCountry(request.getCountry() != null ? request.getCountry() : existing.getCountry());
            newNotice.setTweetId(request.getTweetId());
            newNotice.setId(id);
            newNotice.setContent(request.getContent());

            Notice saved = noticeRepository.save(newNotice);
            return noticeMapper.toResponseTo(saved);
        } else {
            // Обновляем только content
            existing.setContent(request.getContent());
            Notice updated = noticeRepository.save(existing);
            return noticeMapper.toResponseTo(updated);
        }
    }

    public NoticeResponseTo update(String country, Long tweetId, Long id, NoticeRequestTo request) {
        log.info("Updating notice: {}/{}/{}", country, tweetId, id);

        Notice notice = noticeRepository.findByCountryAndTweetIdAndId(country, tweetId, id)
                .orElseThrow(() -> {
                    log.error("Notice not found for update: {}/{}/{}", country, tweetId, id);
                    return new ResourceNotFoundException(
                            String.format("Notice not found with country: %s, tweetId: %d, id: %d", country, tweetId, id)
                    );
                });

        notice.setContent(request.getContent());

        Notice updated = noticeRepository.save(notice);
        log.info("Notice updated successfully: {}/{}/{}", country, tweetId, id);

        return noticeMapper.toResponseTo(updated);
    }

    public void deleteByIdOnly(Long id) {
        log.info("Deleting notice by id only: {}", id);

        Notice notice = noticeRepository.findAll().stream()
                .filter(n -> n.getId().equals(id))
                .findFirst()
                .orElseThrow(() -> {
                    log.error("Notice not found for deletion: {}", id);
                    return new ResourceNotFoundException(
                            String.format("Notice not found with id: %d", id)
                    );
                });

        noticeRepository.delete(notice);
        log.info("Notice deleted successfully: {}", id);
    }

    public void delete(String country, Long tweetId, Long id) {
        log.info("Deleting notice: {}/{}/{}", country, tweetId, id);

        Notice notice = noticeRepository.findByCountryAndTweetIdAndId(country, tweetId, id)
                .orElseThrow(() -> {
                    log.error("Notice not found for deletion: {}/{}/{}", country, tweetId, id);
                    return new ResourceNotFoundException(
                            String.format("Notice not found with country: %s, tweetId: %d, id: %d", country, tweetId, id)
                    );
                });

        noticeRepository.delete(notice);
        log.info("Notice deleted successfully: {}/{}/{}", country, tweetId, id);
    }
}
