package com.example.distcomp.service;

import com.example.distcomp.dto.MessageRequestTo;
import com.example.distcomp.dto.MessageResponseTo;
import com.example.distcomp.exception.ServiceException;
import com.example.distcomp.mapper.MessageMapper;
import com.example.distcomp.model.Issue;
import com.example.distcomp.model.Message;
import com.example.distcomp.repository.IssueRepository;
import com.example.distcomp.repository.MessageRepository;
import com.example.distcomp.utils.ValidationUtils;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;
import java.sql.Timestamp;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MessageService {
    private final MessageRepository messageRepository;
    private final IssueRepository issueRepository;
    private final MessageMapper messageMapper;
    private final RedisTemplate<String, Object> redisTemplate;
    private final ObjectMapper objectMapper = new ObjectMapper();

    private static final long CACHE_TTL_MINUTES = 30;

    public MessageResponseTo createMessage(MessageRequestTo request) {
        validateMessageRequest(request);
        Message entity = messageMapper.toEntity(request);
        entity.setIssue(issueRepository.getReferenceById(request.getIssueId()));
        try {
            entity = messageRepository.save(entity);
            MessageResponseTo response = messageMapper.toResponse(entity);

            redisTemplate.delete("allMessages");
            redisTemplate.opsForValue().set(
                    "message:" + entity.getId(),
                    response,
                    CACHE_TTL_MINUTES,
                    TimeUnit.MINUTES
            );
            return response;
        } catch (DataIntegrityViolationException e) {
            throw new ServiceException("Data integrity violation", 403);
        }
    }

    public List<MessageResponseTo> getAllMessages() {
        String key = "allMessages";
        Object cached = redisTemplate.opsForValue().get(key);

        if (cached != null) {
            return objectMapper.convertValue(cached, new TypeReference<List<MessageResponseTo>>() {});
        }

        List<MessageResponseTo> responseList = messageMapper.listToResponse(messageRepository.findAll());
        redisTemplate.opsForValue().set(key, responseList, CACHE_TTL_MINUTES, TimeUnit.MINUTES);
        return responseList;
    }

    public MessageResponseTo getMessageById(Long id) {
        String key = "message:" + id;
        Object cached = redisTemplate.opsForValue().get(key);

        if (cached != null) {
            return objectMapper.convertValue(cached, MessageResponseTo.class);
        }

        Message message = messageRepository.findById(id)
                .orElseThrow(() -> new ServiceException("Message not found with id: " + id, 404));
        MessageResponseTo response = messageMapper.toResponse(message);
        redisTemplate.opsForValue().set(key, response, CACHE_TTL_MINUTES, TimeUnit.MINUTES);
        return response;
    }

    public MessageResponseTo updateMessage(MessageRequestTo request) {
        validateMessageRequest(request);
        Message entity = messageMapper.toEntity(request);
        if (!messageRepository.existsById(entity.getId())) {
            throw new ServiceException("Message not found with id: " + entity.getId(), 404);
        }
        entity.setIssue(issueRepository.getReferenceById(request.getIssueId()));
        try {
            entity = messageRepository.save(entity);
            MessageResponseTo response = messageMapper.toResponse(entity);

            redisTemplate.delete("message:" + entity.getId());
            redisTemplate.delete("allMessages");
            redisTemplate.opsForValue().set(
                    "message:" + entity.getId(),
                    response,
                    CACHE_TTL_MINUTES,
                    TimeUnit.MINUTES
            );
            return response;
        } catch (DataIntegrityViolationException e) {
            throw new ServiceException("Data integrity violation", 403);
        }
    }

    public void deleteMessage(Long id) {
        if (!messageRepository.existsById(id)) {
            throw new ServiceException("Message not found with id: " + id, 404);
        }
        messageRepository.deleteById(id);
        redisTemplate.delete("message:" + id);
        redisTemplate.delete("allMessages");
    }

    private void validateMessageRequest(MessageRequestTo request) {
        ValidationUtils.validateNotNull(request, "Message");
        ValidationUtils.validateString(request.getContent(), "Text", 2, 2048);
        ValidationUtils.validateNotNegative(request.getIssueId(), "Issue ID");
    }
}
