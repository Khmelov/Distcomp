package com.task.discussion.service.kafka;

import com.task.discussion.config.KafkaConfig;
import com.task.discussion.dto.kafka.NoticeKafkaRequestDto;
import com.task.discussion.dto.kafka.NoticeKafkaResponseDto;
import com.task.discussion.model.Notice;
import com.task.discussion.repository.NoticeRepository;
import com.task.discussion.service.ModerationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class KafkaListenerService {

    private final NoticeRepository noticeRepository;
    private final ModerationService moderationService;
    private final KafkaTemplate<String, NoticeKafkaResponseDto> kafkaTemplate;

    @KafkaListener(topics = KafkaConfig.IN_TOPIC, groupId = "discussion-group")
    public void consume(NoticeKafkaRequestDto request) {
        log.info("Received from InTopic: operation={}, id={}, requestId={}",
                request.getOperation(), request.getId(), request.getRequestId());

        NoticeKafkaResponseDto response = new NoticeKafkaResponseDto();
        response.setRequestId(request.getRequestId());
        response.setOperation(request.getOperation());
        response.setId(request.getId());
        response.setTweetId(request.getTweetId());

        try {
            switch (request.getOperation()) {
                case "CREATE" -> handleCreate(request, response);
                case "UPDATE" -> handleUpdate(request, response);
                case "DELETE" -> handleDelete(request, response);
                case "GET_BY_ID" -> handleGetById(request, response);
                case "GET_ALL" -> handleGetAll(request, response);
                default -> response.setError("Unknown operation");
            }
        } catch (Exception e) {
            log.error("Error processing", e);
            response.setError(e.getMessage());
        }

        sendResponse(request, response);
    }

    private void handleCreate(NoticeKafkaRequestDto req, NoticeKafkaResponseDto resp) {
        String state = moderationService.moderate(req.getContent());

        Notice notice = new Notice();
        notice.setCountry(req.getCountry() != null ? req.getCountry() : "BY");
        notice.setTweetId(req.getTweetId());
        notice.setId(req.getId());
        notice.setContent(req.getContent());
        notice.setState(state);

        Notice saved = noticeRepository.save(notice);

        resp.setId(saved.getId());
        resp.setTweetId(saved.getTweetId());
        resp.setContent(saved.getContent());
        resp.setCountry(saved.getCountry());
        resp.setState(saved.getState());

        log.info("Created notice: id={}, state={}", saved.getId(), saved.getState());
    }

    private void handleUpdate(NoticeKafkaRequestDto req, NoticeKafkaResponseDto resp) {
        Notice existing = noticeRepository.findAll().stream()
                .filter(n -> n.getId().equals(req.getId()))
                .findFirst()
                .orElse(null);

        if (existing == null) {
            resp.setError("Notice not found: " + req.getId());
            return;
        }

        existing.setContent(req.getContent());
        existing.setState(moderationService.moderate(req.getContent()));

        Notice saved = noticeRepository.save(existing);

        resp.setId(saved.getId());
        resp.setTweetId(saved.getTweetId());
        resp.setContent(saved.getContent());
        resp.setCountry(saved.getCountry());
        resp.setState(saved.getState());
    }

    private void handleDelete(NoticeKafkaRequestDto req, NoticeKafkaResponseDto resp) {
        Notice existing = noticeRepository.findAll().stream()
                .filter(n -> n.getId().equals(req.getId()))
                .findFirst()
                .orElse(null);

        if (existing != null) {
            noticeRepository.delete(existing);
            resp.setState("DELETED");
            log.info("Deleted notice: id={}", req.getId());
        } else {
            resp.setError("Notice not found: " + req.getId());
        }
    }

    private void handleGetById(NoticeKafkaRequestDto req, NoticeKafkaResponseDto resp) {
        Notice notice = noticeRepository.findAll().stream()
                .filter(n -> n.getId().equals(req.getId()))
                .findFirst()
                .orElse(null);

        if (notice != null) {
            resp.setId(notice.getId());
            resp.setTweetId(notice.getTweetId());
            resp.setContent(notice.getContent());
            resp.setCountry(notice.getCountry());
            resp.setState(notice.getState());
        } else {
            resp.setError("Notice not found: " + req.getId());
        }
    }

    private void handleGetAll(NoticeKafkaRequestDto req, NoticeKafkaResponseDto resp) {
        List<Notice> notices = noticeRepository.findAll();

        List<NoticeKafkaRequestDto> dtos = notices.stream()
                .map(n -> NoticeKafkaRequestDto.builder()
                        .id(n.getId())
                        .tweetId(n.getTweetId())
                        .content(n.getContent())
                        .country(n.getCountry())
                        .state(n.getState())
                        .build())
                .collect(Collectors.toList());

        resp.setNotices(dtos);
        log.info("Found {} notices", dtos.size());
    }

    private void sendResponse(NoticeKafkaRequestDto req, NoticeKafkaResponseDto resp) {
        String key = req.getTweetId() != null ? req.getTweetId().toString() : "default";
        kafkaTemplate.send(KafkaConfig.OUT_TOPIC, key, resp);
        log.info("Sent response to OutTopic: requestId={}, operation={}", resp.getRequestId(), resp.getOperation());
    }
}