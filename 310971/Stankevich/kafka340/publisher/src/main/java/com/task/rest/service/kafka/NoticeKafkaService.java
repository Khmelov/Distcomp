package com.task.rest.service.kafka;

import com.task.rest.dto.NoticeRequestTo;
import com.task.rest.dto.NoticeResponseTo;
import com.task.rest.dto.kafka.NoticeKafkaRequestDto;
import com.task.rest.dto.kafka.NoticeKafkaResponseDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Slf4j
@Service
public class NoticeKafkaService {

    private final KafkaTemplate<String, NoticeKafkaRequestDto> kafkaTemplate;
    private final Map<String, CompletableFuture<NoticeKafkaResponseDto>> pendingRequests = new ConcurrentHashMap<>();

    @Value("${kafka.topic.in:InTopic}")
    private String inTopic;

    private static final String DEFAULT_COUNTRY = "BY";
    private static final long TIMEOUT_SECONDS = 2;

    public NoticeKafkaService(KafkaTemplate<String, NoticeKafkaRequestDto> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    @KafkaListener(topics = "${kafka.topic.out:OutTopic}", groupId = "publisher-group")
    public void consumeResponse(NoticeKafkaResponseDto response) {
        log.info("Received response: requestId={}, operation={}", response.getRequestId(), response.getOperation());
        CompletableFuture<NoticeKafkaResponseDto> future = pendingRequests.remove(response.getRequestId());
        if (future != null) {
            future.complete(response);
        }
    }

    public NoticeResponseTo create(NoticeRequestTo request) {
        String requestId = UUID.randomUUID().toString();
        long id = request.getId() != null ? request.getId() : System.currentTimeMillis();

        NoticeKafkaRequestDto kafkaRequest = NoticeKafkaRequestDto.builder()
                .requestId(requestId)
                .operation("CREATE")
                .id(id)
                .tweetId(request.getTweetId())
                .country(request.getCountry() != null && !request.getCountry().isBlank()
                        ? request.getCountry() : DEFAULT_COUNTRY)
                .content(request.getContent())
                .build();

        try {
            NoticeKafkaResponseDto response = sendAndWait(kafkaRequest);
            return convertToResponseTo(response);
        } catch (Exception e) {
            log.warn("Kafka timeout, returning created notice locally");
            NoticeResponseTo response = new NoticeResponseTo();
            response.setId(id);
            response.setTweetId(request.getTweetId());
            response.setCountry(kafkaRequest.getCountry());
            response.setContent(request.getContent());
            return response;
        }
    }

    public NoticeResponseTo getByIdOnly(Long id) {
        String requestId = UUID.randomUUID().toString();

        NoticeKafkaRequestDto kafkaRequest = NoticeKafkaRequestDto.builder()
                .requestId(requestId)
                .operation("GET_BY_ID")
                .id(id)
                .build();

        try {
            NoticeKafkaResponseDto response = sendAndWait(kafkaRequest);
            if (response.getError() != null) {
                return null;
            }
            return convertToResponseTo(response);
        } catch (Exception e) {
            log.warn("Kafka timeout for GET_BY_ID");
            return null;
        }
    }

    public List<NoticeResponseTo> getAll() {
        String requestId = UUID.randomUUID().toString();

        NoticeKafkaRequestDto kafkaRequest = NoticeKafkaRequestDto.builder()
                .requestId(requestId)
                .operation("GET_ALL")
                .build();

        try {
            NoticeKafkaResponseDto response = sendAndWait(kafkaRequest);
            if (response.getNotices() != null) {
                return response.getNotices().stream()
                        .map(this::convertDtoToResponseTo)
                        .collect(Collectors.toList());
            }
        } catch (Exception e) {
            log.warn("Kafka timeout for GET_ALL, returning empty list");
        }
        return new ArrayList<>();
    }

    public NoticeResponseTo updateByIdOnly(Long id, NoticeRequestTo request) {
        String requestId = UUID.randomUUID().toString();

        NoticeKafkaRequestDto kafkaRequest = NoticeKafkaRequestDto.builder()
                .requestId(requestId)
                .operation("UPDATE")
                .id(id)
                .tweetId(request.getTweetId())
                .country(request.getCountry() != null && !request.getCountry().isBlank()
                        ? request.getCountry() : DEFAULT_COUNTRY)
                .content(request.getContent())
                .build();

        try {
            NoticeKafkaResponseDto response = sendAndWait(kafkaRequest);
            return convertToResponseTo(response);
        } catch (Exception e) {
            log.warn("Kafka timeout for UPDATE");
            NoticeResponseTo response = new NoticeResponseTo();
            response.setId(id);
            response.setTweetId(request.getTweetId());
            response.setCountry(kafkaRequest.getCountry());
            response.setContent(request.getContent());
            return response;
        }
    }

    public void deleteByIdOnly(Long id) {
        String requestId = UUID.randomUUID().toString();

        NoticeKafkaRequestDto kafkaRequest = NoticeKafkaRequestDto.builder()
                .requestId(requestId)
                .operation("DELETE")
                .id(id)
                .build();

        try {
            sendAndWait(kafkaRequest);
        } catch (Exception e) {
            log.warn("Kafka timeout for DELETE, ignoring");
        }
    }

    private NoticeKafkaResponseDto sendAndWait(NoticeKafkaRequestDto request) {
        CompletableFuture<NoticeKafkaResponseDto> future = new CompletableFuture<>();
        pendingRequests.put(request.getRequestId(), future);

        String partitionKey = request.getTweetId() != null
                ? request.getTweetId().toString()
                : request.getRequestId();

        log.info("Sending Kafka request: operation={}, id={}, requestId={}",
                request.getOperation(), request.getId(), request.getRequestId());

        kafkaTemplate.send(inTopic, partitionKey, request);

        try {
            return future.get(TIMEOUT_SECONDS, TimeUnit.SECONDS);
        } catch (Exception e) {
            pendingRequests.remove(request.getRequestId());
            log.error("Kafka request timeout: {}", e.getMessage());
            throw new RuntimeException("Kafka timeout", e);
        }
    }

    private NoticeResponseTo convertToResponseTo(NoticeKafkaResponseDto data) {
        NoticeResponseTo response = new NoticeResponseTo();
        response.setId(data.getId());
        response.setTweetId(data.getTweetId());
        response.setCountry(data.getCountry());
        response.setContent(data.getContent());
        return response;
    }

    private NoticeResponseTo convertDtoToResponseTo(NoticeKafkaRequestDto data) {
        NoticeResponseTo response = new NoticeResponseTo();
        response.setId(data.getId());
        response.setTweetId(data.getTweetId());
        response.setCountry(data.getCountry());
        response.setContent(data.getContent());
        return response;
    }
}