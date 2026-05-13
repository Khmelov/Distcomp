package com.example.news.service;

import com.example.common.dto.ArticleResponseTo;
import com.example.common.dto.MessageRequestTo;
import com.example.common.dto.MessageResponseTo;
import com.example.common.dto.model.enums.MessageState;
import com.example.news.repository.WriterRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.time.Duration;
import java.util.Arrays;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MessageService {

    private final RestTemplate restTemplate;
    private final String DISCUSSION_URL = "http://localhost:24130/api/v1.0/messages";
    private final KafkaTemplate<String, MessageResponseTo> kafkaTemplate;
    private static final String IN_TOPIC = "InTopic";
    private final RedisTemplate<String, Object> redisTemplate;
    private final WriterRepository writerRepository;
    private final ArticleService articleService;

    public MessageResponseTo create(MessageRequestTo request) {
        Long generatedId = System.currentTimeMillis();

        MessageResponseTo pendingMessage = new MessageResponseTo(
                generatedId,
                request.articleId(),
                request.content(),
                MessageState.PENDING
        );

        kafkaTemplate.send(IN_TOPIC, String.valueOf(request.articleId()), pendingMessage);

        return pendingMessage;
    }

@KafkaListener(topics = "OutTopic", groupId = "publisher-group")
public void listenOutTopic(@Payload MessageResponseTo messageDto) {
    String cacheKey = "messages::" + messageDto.id();
    redisTemplate.opsForValue().set(cacheKey, messageDto, Duration.ofMinutes(5));

    System.out.println("Message cached from Kafka: " + messageDto.id());
}

    public List<MessageResponseTo> findAll(int page, int size, String sortBy) {
        MessageResponseTo[] response = restTemplate.getForObject(DISCUSSION_URL, MessageResponseTo[].class);
        return response != null ? Arrays.asList(response) : List.of();
    }

    public MessageResponseTo findById(Long id) {
        String cacheKey = "messages::" + id;

        MessageResponseTo cached = (MessageResponseTo) redisTemplate.opsForValue().get(cacheKey);
        if (cached != null) return cached;

        kafkaTemplate.send("InTopic", id.toString(), new MessageResponseTo(id, null, null, null));

        long start = System.currentTimeMillis();
        while (System.currentTimeMillis() - start < 1000) {
            cached = (MessageResponseTo) redisTemplate.opsForValue().get(cacheKey);
            if (cached != null) return cached;

            try { Thread.sleep(100); } catch (InterruptedException e) { Thread.currentThread().interrupt(); }
        }

        return restTemplate.getForObject(DISCUSSION_URL + "/" + id, MessageResponseTo.class);
    }

    public void delete(Long id) {
        redisTemplate.delete("messages::" + id);
        restTemplate.delete(DISCUSSION_URL + "/" + id);
    }

    public MessageResponseTo update(Long id, MessageRequestTo request) {
        redisTemplate.delete("messages::" + id);
        restTemplate.put(DISCUSSION_URL + "/" + id, request);

        return new MessageResponseTo(id, request.articleId(), request.content(), MessageState.APPROVE);
    }

    @Transactional(readOnly = true)
    public boolean isOwner(Long messageId, String currentLogin) {
        try {
            MessageResponseTo message = this.findById(messageId);
            if (message == null) return false;

            ArticleResponseTo article = articleService.findById(message.articleId());
            if (article == null || article.writerId() == null) return false;

            return writerRepository.findByLogin(currentLogin)
                    .map(writer -> article.writerId().equals(writer.getId()))
                    .orElse(false);
        } catch (Exception e) {
            return false;
        }
    }
}