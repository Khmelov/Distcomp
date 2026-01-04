package com.aitor.publisher.service;

import com.aitor.publisher.dto.MessageRequestTo;
import com.aitor.publisher.dto.MessageResponseTo;
import com.aitor.publisher.exception.EntityNotExistsException;
import com.aitor.publisher.kafka.KafkaConsumerService;
import com.aitor.publisher.kafka.KafkaProducerService;
import com.aitor.publisher.model.Issue;
import com.aitor.publisher.model.Message;
import com.aitor.publisher.redis.RedisService;
import com.aitor.publisher.repository.IssueRepository;
import com.aitor.publisher.repository.MessageRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeoutException;

@Service
@RequiredArgsConstructor
public class MessageService {
    private final IssueRepository issueRepository;
    private final KafkaProducerService kafkaProducerService;
    private final KafkaConsumerService kafkaConsumerService;
    private final RedisService redisService;

    public MessageResponseTo add(MessageRequestTo requestBody){
        getIssue(requestBody);
        var key = "P";
        kafkaProducerService.sendMessage(requestBody, key);
        try {
            var response = convertResponse(kafkaConsumerService.waitForResponse(key));
            redisService.setValue(response.getId().toString(), response);
            return response;
        } catch (TimeoutException e) {
            throw new RuntimeException(e);
        }
    }

    public MessageResponseTo set(Long id, MessageRequestTo requestBody){
        getIssue(requestBody);
        var key = "U".concat(id.toString());
        kafkaProducerService.sendMessage(requestBody, key);
        try {
            var response = convertResponse(kafkaConsumerService.waitForResponse(key));
            redisService.setValue(id.toString(), response);
            return response;
        } catch (TimeoutException e) {
            throw new RuntimeException(e);
        }
    }

    public MessageResponseTo get(Long id) {
        var key = id.toString();
        var cachedResponse = redisService.getValue(key);
        if (cachedResponse != null)
            return cachedResponse;
        kafkaProducerService.sendMessage(key);
        try {
            return convertResponse(kafkaConsumerService.waitForResponse(key));
        } catch (TimeoutException e) {
            throw new RuntimeException(e);
        }
    }

    public List<MessageResponseTo> getAll(){
        var key = "G";
        kafkaProducerService.sendMessage(key);
        try {
            return kafkaConsumerService.waitForResponse(key);
        } catch (TimeoutException e) {
            throw new RuntimeException(e);
        }
    }

    public MessageResponseTo remove(Long id) {
        var key = "D".concat(id.toString());
        kafkaProducerService.sendMessage(key);
        try {
            return convertResponse(kafkaConsumerService.waitForResponse(key));
        } catch (TimeoutException e) {
            throw new RuntimeException(e);
        }
    }

    private MessageResponseTo convertResponse(List<MessageResponseTo> responseList){
        ObjectMapper mapper = new ObjectMapper();
        if (responseList.getFirst() instanceof Map) {
            var response = mapper.convertValue(responseList.getFirst(), MessageResponseTo.class);
            if (response.getId() == null)
                throw new EntityNotExistsException();
            return response;
        } else {
            try {
                return mapper.readValue(responseList.getFirst().toString(), MessageResponseTo.class);
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private Issue getIssue(MessageRequestTo requestBody){
        var entity = issueRepository.findById(requestBody.getIssueId());
        if (entity.isPresent())
            return entity.get();
        throw new EntityNotExistsException();
    }

    private MessageResponseTo toResponse(Message entity){
        return new MessageResponseTo(
                entity.getId(),
                entity.getIssueId().getId(),
                entity.getContent());
    }
}
