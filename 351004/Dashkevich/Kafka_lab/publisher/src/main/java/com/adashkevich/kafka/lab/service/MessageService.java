package com.adashkevich.kafka.lab.service;

import com.adashkevich.kafka.lab.dto.request.MessageRequestTo;
import com.adashkevich.kafka.lab.dto.response.MessageResponseTo;
import com.adashkevich.kafka.lab.exception.ValidationException;
import com.adashkevich.kafka.lab.kafka.KafkaOperation;
import com.adashkevich.kafka.lab.kafka.KafkaReplyMessage;
import com.adashkevich.kafka.lab.kafka.KafkaRequestMessage;
import com.adashkevich.kafka.lab.kafka.KafkaRequestReplyClient;
import com.adashkevich.kafka.lab.repository.NewsRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

@Service
public class MessageService {

    private final KafkaRequestReplyClient kafkaClient;
    private final NewsRepository newsRepo;
    private final AtomicLong idSequence = new AtomicLong(System.currentTimeMillis());

    public MessageService(KafkaRequestReplyClient kafkaClient, NewsRepository newsRepo) {
        this.kafkaClient = kafkaClient;
        this.newsRepo = newsRepo;
    }

    public MessageResponseTo create(MessageRequestTo dto) {
        ensureNewsExists(dto.newsId);
        Long id = idSequence.incrementAndGet();

        KafkaRequestMessage request = new KafkaRequestMessage();
        request.setOperation(KafkaOperation.CREATE);
        request.setId(id);
        request.setNewsId(dto.newsId);
        request.setContent(dto.content);
        kafkaClient.sendAndReceive(request);

        MessageResponseTo response = new MessageResponseTo();
        response.id = id;
        response.newsId = dto.newsId;
        response.content = dto.content;
        response.state = "PENDING";
        return response;
    }

    public List<MessageResponseTo> getAll() {
        KafkaRequestMessage request = new KafkaRequestMessage();
        request.setOperation(KafkaOperation.READ_ALL);
        request.setNewsId(0L);
        KafkaReplyMessage reply = kafkaClient.sendAndReceive(request);
        return reply.getMessages();
    }

    public MessageResponseTo getById(Long id) {
        KafkaRequestMessage request = new KafkaRequestMessage();
        request.setOperation(KafkaOperation.READ_BY_ID);
        request.setId(id);
        request.setNewsId(0L);
        return kafkaClient.sendAndReceive(request).getMessage();
    }

    public MessageResponseTo update(Long id, MessageRequestTo dto) {
        ensureNewsExists(dto.newsId);
        KafkaRequestMessage request = new KafkaRequestMessage();
        request.setOperation(KafkaOperation.UPDATE);
        request.setId(id);
        request.setNewsId(dto.newsId);
        request.setContent(dto.content);
        return kafkaClient.sendAndReceive(request).getMessage();
    }

    public void delete(Long id) {
        KafkaRequestMessage request = new KafkaRequestMessage();
        request.setOperation(KafkaOperation.DELETE);
        request.setId(id);
        request.setNewsId(0L);
        kafkaClient.sendAndReceive(request);
    }

    public List<MessageResponseTo> getByNewsId(Long newsId) {
        ensureNewsExists(newsId);
        KafkaRequestMessage request = new KafkaRequestMessage();
        request.setOperation(KafkaOperation.READ_BY_NEWS_ID);
        request.setNewsId(newsId);
        return kafkaClient.sendAndReceive(request).getMessages();
    }

    public void deleteByNewsId(Long newsId) {
        KafkaRequestMessage request = new KafkaRequestMessage();
        request.setOperation(KafkaOperation.DELETE_BY_NEWS_ID);
        request.setNewsId(newsId);
        kafkaClient.sendAndReceive(request);
    }

    private void ensureNewsExists(Long newsId) {
        if (newsId == null || !newsRepo.existsById(newsId)) {
            throw new ValidationException("newsId does not exist", "40020");
        }
    }
}
