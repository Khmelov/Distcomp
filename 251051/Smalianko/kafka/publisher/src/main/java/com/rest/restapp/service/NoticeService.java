package com.rest.restapp.service;

import com.common.KafkaOperation;
import com.common.NoticeAsyncResponse;
import com.common.NoticeMessage;
import com.common.NoticeResponseToDto;
import com.common.NoticeState;
import com.rest.restapp.client.DiscussionClient;
import com.rest.restapp.dto.request.NoticeRequestToDto;
import com.rest.restapp.exception.NotFoundException;
import com.rest.restapp.exception.ValidationException;
import com.rest.restapp.mapper.NoticeMapper;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class NoticeService {

    DiscussionClient discussionClient;
    StreamBridge streamBridge;
    CompositeIdCodecService codecService;
    NoticeMapper mapper;

    private final Map<String, CompletableFuture<List<NoticeResponseToDto>>> pending = new ConcurrentHashMap<>();

    public NoticeResponseToDto createNotice(NoticeRequestToDto requestTo) {
        validateNoticeRequest(requestTo);

        var key = codecService.encode("RU", requestTo.issueId(), generateId());

        var message = NoticeMessage.builder()
                .operation(KafkaOperation.CREATE)
                .state(NoticeState.PENDING)
                .id(key)
                .correlationId(UUID.randomUUID().toString())
                .issueId(requestTo.issueId())
                .content(requestTo.content())
                .build();

        sendToDiscussion(message);
        return mapper.mapToResponse(message);
    }

    public NoticeResponseToDto getNoticeById(Long id) {
        var correlationId = UUID.randomUUID().toString();
        var future = new CompletableFuture<List<NoticeResponseToDto>>();
        var message = NoticeMessage.builder()
                .operation(KafkaOperation.GET)
                .id(id)
                .correlationId(correlationId)
                .build();
        sendToDiscussion(message);
        pending.put(correlationId, future);

        try {
            return future.get(2, TimeUnit.SECONDS).stream()
                    .findFirst()
                    .orElseThrow();
        } catch (Exception e) {
            pending.remove(correlationId);
            throw new NotFoundException("Failed to fetch notice or not found notice by id" + id.toString());
        }
    }

    public List<NoticeResponseToDto> getAllNotices() {
        var correlationId = UUID.randomUUID().toString();
        var future = new CompletableFuture<List<NoticeResponseToDto>>();
        var message = NoticeMessage.builder()
                .operation(KafkaOperation.GET_ALL)
                .correlationId(correlationId)
                .build();
        sendToDiscussion(message);
        pending.put(correlationId, future);

        try {
            return future.get(2, TimeUnit.SECONDS);
        } catch (Exception e) {
            pending.remove(correlationId);
            throw new NotFoundException("Failed to fetch notices or not found notice by id");
        }
    }

    public NoticeResponseToDto updateNotice(NoticeRequestToDto requestTo) {
        validateNoticeRequest(requestTo);
        var correlationId = UUID.randomUUID().toString();
        var future = new CompletableFuture<List<NoticeResponseToDto>>();
        var message = NoticeMessage.builder()
                .operation(KafkaOperation.UPDATE)
                .state(NoticeState.PENDING)
                .id(requestTo.id())
                .correlationId(correlationId)
                .issueId(requestTo.issueId())
                .content(requestTo.content())
                .build();

        sendToDiscussion(message);
        pending.put(correlationId, future);
        try {
            return future.get(2, TimeUnit.SECONDS).stream()
                    .findFirst()
                    .orElseThrow();
        } catch (Exception e) {
            pending.remove(correlationId);
            throw new NotFoundException("Failed to fetch notices or not found notice by id");
        }
    }

    public void deleteNotice(Long id) {
        var message = NoticeMessage.builder()
                .operation(KafkaOperation.DELETE)
                .id(id)
                .correlationId(UUID.randomUUID().toString())
                .build();
        sendToDiscussion(message);
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

    public void handleResponse(NoticeAsyncResponse message) {
        var future = pending.remove(message.correlationId());
        if (future != null) {
            future.complete(message.noticeMessages());
        }
    }

    private void sendToDiscussion(NoticeMessage message) {
        streamBridge.send("to-discussion-out-0", message);
    }

    private long generateId() {
        return Math.abs(UUID.randomUUID ().getMostSignificantBits());
    }
}
