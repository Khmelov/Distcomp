package com.socialnetwork.service.impl;

import com.socialnetwork.dto.kafka.KafkaMessageRequest;
import com.socialnetwork.dto.kafka.KafkaMessageResponse;
import com.socialnetwork.dto.request.MessageRequestTo;
import com.socialnetwork.dto.response.MessageResponseTo;
import com.socialnetwork.service.KafkaProducerService;
import com.socialnetwork.service.MessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

@Service
public class MessageServiceImpl implements MessageService {

    @Autowired
    private KafkaProducerService kafkaProducerService;

    @Override
    public List<MessageResponseTo> getAll() {
        KafkaMessageRequest request = new KafkaMessageRequest();
        request.setRequestId(UUID.randomUUID());
        request.setOperation("GET_ALL");

        return processListRequest(request);
    }

    @Override
    public MessageResponseTo getById(Long id) {
        KafkaMessageRequest request = new KafkaMessageRequest();
        request.setRequestId(UUID.randomUUID());
        request.setOperation("GET");
        request.setMessageId(id);

        return processSingleRequest(request);
    }

    @Override
    public MessageResponseTo create(MessageRequestTo requestDto) {
        KafkaMessageRequest request = new KafkaMessageRequest();
        request.setRequestId(UUID.randomUUID());
        request.setOperation("CREATE");
        request.setCountry(requestDto.getCountry() != null ? requestDto.getCountry() : "US");
        request.setTweetId(requestDto.getTweetId());
        request.setContent(requestDto.getContent());

        return processSingleRequest(request);
    }

    @Override
    public MessageResponseTo update(Long id, MessageRequestTo requestDto) {
        KafkaMessageRequest request = new KafkaMessageRequest();
        request.setRequestId(UUID.randomUUID());
        request.setOperation("UPDATE");
        request.setMessageId(id);
        request.setCountry(requestDto.getCountry() != null ? requestDto.getCountry() : "US");
        request.setTweetId(requestDto.getTweetId());
        request.setContent(requestDto.getContent());

        return processSingleRequest(request);
    }

    @Override
    public void delete(Long id) {
        KafkaMessageRequest request = new KafkaMessageRequest();
        request.setRequestId(UUID.randomUUID());
        request.setOperation("DELETE");
        request.setMessageId(id);

        try {
            CompletableFuture<KafkaMessageResponse> future = kafkaProducerService.sendMessageRequest(request);
            KafkaMessageResponse response = future.get(2, TimeUnit.SECONDS);

            if (!response.isSuccess()) {
                throw new RuntimeException("Failed to delete message: " + response.getError());
            }
        } catch (InterruptedException | ExecutionException | TimeoutException e) {
            throw new RuntimeException("Failed to delete message: " + e.getMessage());
        }
    }

    @Override
    public List<MessageResponseTo> getByTweetId(Long tweetId) {
        KafkaMessageRequest request = new KafkaMessageRequest();
        request.setRequestId(UUID.randomUUID());
        request.setOperation("GET_BY_TWEET");
        request.setTweetId(tweetId);

        return processListRequest(request);
    }

    private MessageResponseTo processSingleRequest(KafkaMessageRequest request) {
        try {
            CompletableFuture<KafkaMessageResponse> future = kafkaProducerService.sendMessageRequest(request);
            KafkaMessageResponse response = future.get(2, TimeUnit.SECONDS);

            if (response.isSuccess()) {
                MessageResponseTo dto = new MessageResponseTo();
                dto.setId(response.getMessageId());
                dto.setTweetId(response.getTweetId());
                dto.setContent(response.getContent());
                return dto;
            } else {
                throw new RuntimeException("Operation failed: " + response.getError());
            }
        } catch (InterruptedException | ExecutionException | TimeoutException e) {
            throw new RuntimeException("Operation failed: " + e.getMessage());
        }
    }

    private List<MessageResponseTo> processListRequest(KafkaMessageRequest request) {
        try {
            CompletableFuture<KafkaMessageResponse> future = kafkaProducerService.sendMessageRequest(request);
            KafkaMessageResponse response = future.get(2, TimeUnit.SECONDS);

            if (response.isSuccess() && response.getMessages() != null) {
                return response.getMessages().stream()
                        .map(kafkaResponse -> {
                            MessageResponseTo dto = new MessageResponseTo();
                            dto.setId(kafkaResponse.getMessageId());
                            dto.setTweetId(kafkaResponse.getTweetId());
                            dto.setContent(kafkaResponse.getContent());
                            return dto;
                        })
                        .toList();
            } else {
                throw new RuntimeException("Operation failed: " + response.getError());
            }
        } catch (InterruptedException | ExecutionException | TimeoutException e) {
            throw new RuntimeException("Operation failed: " + e.getMessage());
        }
    }
}