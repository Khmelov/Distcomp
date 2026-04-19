package com.example.discussion.service;

import com.example.common.dto.MessageRequestTo;
import com.example.common.dto.MessageResponseTo;
import com.example.common.dto.model.enums.MessageState;
import com.example.common.exception.MessageNotFoundException;
import com.example.discussion.mapper.MessageMapper;
import com.example.discussion.model.Message;
import com.example.discussion.model.MessageKey;
import com.example.discussion.repository.MessageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MessageService {

    private final MessageRepository messageRepository;
    private final MessageMapper messageMapper;
    private final KafkaTemplate<String, MessageResponseTo> kafkaTemplate;
    private static final String IN_TOPIC = "InTopic";

    @KafkaListener(topics = "InTopic", groupId = "discussion-group")
    public void listenInTopic(MessageResponseTo messageDto) {
        MessageState finalState = MessageState.APPROVE;

        String content = messageDto.content().toLowerCase();

        if (content.contains("спам") || content.contains("реклама")) {
            finalState = MessageState.DECLINE;
        } else if (content.length() < 3) {
            finalState = MessageState.DECLINE;
        }

        Message messageEntity = Message.builder()
                .key(new MessageKey(messageDto.articleId(), messageDto.id()))
                .content(messageDto.content())
                .state(finalState)
                .build();
        messageRepository.save(messageEntity);

        MessageResponseTo updatedResponse = new MessageResponseTo(
                messageDto.id(), messageDto.articleId(), messageDto.content(), finalState
        );
        kafkaTemplate.send("OutTopic", String.valueOf(updatedResponse.articleId()), updatedResponse);
    }

    public MessageResponseTo create(MessageRequestTo request) {
        Message message = messageMapper.toEntity(request);

        MessageKey key = new MessageKey(request.articleId(), System.currentTimeMillis());
        message.setKey(key);

        Message saved = messageRepository.save(message);
        return messageMapper.toResponse(saved);
    }

    public List<MessageResponseTo> findAll() {
        return messageRepository.findAll().stream()
                .map(messageMapper::toResponse)
                .collect(Collectors.toList());
    }

    public List<MessageResponseTo> findAllByArticleId(Long articleId) {
        return messageRepository.findAllByKeyArticleId(articleId).stream()
                .map(messageMapper::toResponse)
                .collect(Collectors.toList());
    }

    public MessageResponseTo findById(Long id) {
        return messageRepository.findByKeyId(id)
                .map(messageMapper::toResponse)
                .orElseThrow(() -> new MessageNotFoundException("Message not found with id: " + id));
    }

    public MessageResponseTo update(Long id, MessageRequestTo request) {
        Message existingMessage = messageRepository.findByKeyId(id)
                .orElseThrow(() -> new MessageNotFoundException("Message not found with id: " + id));

        existingMessage.setContent(request.content());

        Message saved = messageRepository.save(existingMessage);
        return messageMapper.toResponse(saved);
    }

    public void delete(Long id) {
        Message message = messageRepository.findByKeyId(id)
                .orElseThrow(() -> new RuntimeException("Message not found"));
        messageRepository.delete(message);
    }
}