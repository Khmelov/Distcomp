package com.example.forum.service.impl;

import com.example.forum.dto.request.PostRequestTo;
import com.example.forum.dto.response.PostResponseTo;
import com.example.forum.entity.PostState;
import com.example.forum.exception.BadRequestException;
import com.example.forum.exception.NotFoundException;
import com.example.forum.repository.TopicRepository;
import com.example.forum.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.requestreply.ReplyingKafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestClient;
import com.example.forum.dto.RestPageImpl;
import org.springframework.kafka.core.KafkaTemplate;
import java.util.List;
import java.util.UUID;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.header.internals.RecordHeader;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.kafka.requestreply.RequestReplyFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

@Service
@RequiredArgsConstructor
public class PostServiceImpl implements PostService {

    private final RestClient postClient;
    private final TopicRepository topicRepository;
    private final ReplyingKafkaTemplate<String, Object, Object> replyingKafkaTemplate;

    // Единый метод для всех запросов
    private PostResponseTo sendKafkaRequest(String topic, String key, Object payload) {
        ProducerRecord<String, Object> record = new ProducerRecord<>(topic, key, payload);
        // Обязательно указываем reply-topic
        record.headers().add(new RecordHeader(KafkaHeaders.REPLY_TOPIC, "OutTopic".getBytes()));

        try {
            // Увеличиваем таймаут до 10 секунд!
            RequestReplyFuture<String, Object, Object> replyFuture = replyingKafkaTemplate.sendAndReceive(record);
            ConsumerRecord<String, Object> consumerRecord = replyFuture.get(10, TimeUnit.SECONDS);
            return (PostResponseTo) consumerRecord.value();
        } catch (Exception e) {
            throw new RuntimeException("Kafka communication failed (timeout or error)", e);
        }
    }

    public PostResponseTo create(PostRequestTo request) {
        Long newPostId = generateId();
        return sendKafkaRequest("InTopic", newPostId.toString(), request);
    }

    public PostResponseTo getById(Long id) {
        // Отправляем объект, а не строку "GET_REQUEST"
        PostRequestTo request = new PostRequestTo();
        request.setTopicId(id); // Используем ID как временный topicId или создайте специальный DTO
        request.setContent("FETCH");

        return sendKafkaRequest("InTopic", id.toString(), request);
    }

    private Long generateId() {
        return UUID.randomUUID().getMostSignificantBits() & Long.MAX_VALUE;
    }

    @Override
    public Page<PostResponseTo> getAll(Long topicId, Pageable pageable) {
        // 1. Получаем List вместо RestPageImpl
        List<PostResponseTo> posts = postClient.get()
                .uri(uriBuilder -> uriBuilder
                        .queryParam("topicId", topicId)
                        // Параметры page/size можно оставить,
                        // даже если discussion их игнорирует, это не сломает запрос
                        .build())
                .retrieve()
                .body(new ParameterizedTypeReference<List<PostResponseTo>>() {});

        // 2. Оборачиваем полученный список в PageImpl,
        // чтобы не ломать логику в publisher, которая ожидает Page
        return new PageImpl<>(posts, pageable, posts.size());
    }

    @Override
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
}