package com.example.forum.service.impl;

import com.example.forum.dto.request.PostRequestTo;
import com.example.forum.dto.response.PostResponseTo;
import com.example.forum.exception.BadRequestException;
import com.example.forum.exception.NotFoundException;
import com.example.forum.repository.TopicRepository;
import com.example.forum.service.PostService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.header.internals.RecordHeader;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.requestreply.ReplyingKafkaTemplate;
import org.springframework.kafka.requestreply.RequestReplyFuture;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestClient;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@RequiredArgsConstructor
@CacheConfig(cacheNames = "posts")
public class PostServiceImpl implements PostService {

    private final RestClient postClient;
    private final TopicRepository topicRepository;
    private final ReplyingKafkaTemplate<String, Object, Object> replyingKafkaTemplate;

    private PostResponseTo sendKafkaRequest(String topic, String key, Object payload) {
        ProducerRecord<String, Object> record = new ProducerRecord<>(topic, key, payload);
        record.headers().add(new RecordHeader(KafkaHeaders.REPLY_TOPIC, "OutTopic".getBytes()));

        try {
            RequestReplyFuture<String, Object, Object> replyFuture = replyingKafkaTemplate.sendAndReceive(record);
            ConsumerRecord<String, Object> consumerRecord = replyFuture.get(10, TimeUnit.SECONDS);
            return (PostResponseTo) consumerRecord.value();
        } catch (Exception e) {
            throw new RuntimeException("Kafka communication failed (timeout or error)", e);
        }
    }

    @Override
    @CachePut(key = "#result.id")
    public PostResponseTo create(PostRequestTo request) {
        validate(request);
        return sendKafkaRequest("InTopic", generateId().toString(), request);
    }

    @Override
    @Cacheable(key = "#id", unless = "#result == null")
    public PostResponseTo getById(Long topicId, Long id) {
        PostRequestTo request = new PostRequestTo();
        request.setTopicId(topicId);
        request.setContent("FETCH");

        return sendKafkaRequest("InTopic", id.toString(), request);
    }

    @Override
    public Page<PostResponseTo> getAll(Long topicId, Pageable pageable) {
        List<PostResponseTo> posts = postClient.get()
                .uri(uriBuilder -> uriBuilder
                        .queryParam("topicId", topicId)
                        .build())
                .retrieve()
                .body(new ParameterizedTypeReference<List<PostResponseTo>>() {});

        return new PageImpl<>(posts != null ? posts : List.of(), pageable, posts != null ? posts.size() : 0);
    }

    @Override
    @CacheEvict(key = "#id")
    public PostResponseTo update(Long id, PostRequestTo request) {
        validate(request);

        if (!topicRepository.existsById(request.getTopicId())) {
            throw new NotFoundException("Topic not found", "40434");
        }

        return postClient.put()
                .uri("/{id}", id)
                .contentType(MediaType.APPLICATION_JSON)
                .body(request)
                .retrieve()
                .body(PostResponseTo.class);
    }

    @Override
    @CacheEvict(key = "#id")
    public void delete(Long id) {
        postClient.delete()
                .uri("/{id}", id)
                .retrieve()
                .onStatus(status -> status.value() == 404, (req, res) -> {
                    throw new NotFoundException("Post not found", "40431");
                })
                .toBodilessEntity();
    }

    private void validate(PostRequestTo request) {
        if (request.getTopicId() == null) {
            throw new BadRequestException("TopicId is required", "40031");
        }
        if (!StringUtils.hasText(request.getContent()) ||
                request.getContent().length() < 2 ||
                request.getContent().length() > 2048) {
            throw new BadRequestException("Invalid content", "40032");
        }
    }

    private Long generateId() {
        return UUID.randomUUID().getMostSignificantBits() & Long.MAX_VALUE;
    }

    @KafkaListener(topics = "OutTopic", groupId = "test-group-publisher")
    public void debugListener(@Payload PostResponseTo response,
                              @Header(KafkaHeaders.CORRELATION_ID) String correlationId) {
        log.info("Shpien saw the message from OutTopic! ID: {}, CorrelationID: {}", response.getId(), correlationId);
    }
}