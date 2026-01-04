package com.blog.discussion.service.impl;

import com.blog.discussion.config.IdGenerator;
import com.blog.discussion.dto.request.MessageRequestTo;
import com.blog.discussion.dto.response.MessageResponseTo;
import com.blog.discussion.exception.ResourceNotFoundException;
import com.blog.discussion.mapper.MessageMapper;
import com.blog.discussion.model.Message;
import com.blog.discussion.repository.MessageRepository;
import com.blog.discussion.service.MessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class MessageServiceImpl implements MessageService {

    @Autowired
    private MessageRepository messageRepository;

    @Autowired
    private MessageMapper messageMapper;

    @Autowired
    private IdGenerator idGenerator;

    // Страна по умолчанию (можно вынести в конфигурацию)
    private static final String DEFAULT_COUNTRY = "global";

    @Override
    public List<MessageResponseTo> getAllMessages() {
        // Возвращаем все сообщения для "global" страны
        List<Message> messages = messageRepository.findByCountry(DEFAULT_COUNTRY);
        return messages.stream()
                .map(messageMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<MessageResponseTo> getMessagesByTopic(String country, Long topicId) {
        List<Message> messages = messageRepository.findByCountryAndTopicId(
                getCountryOrDefault(country),
                topicId
        );
        return messages.stream()
                .map(messageMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public Slice<MessageResponseTo> getMessagesByTopic(String country, Long topicId, Pageable pageable) {
        Slice<Message> messages = messageRepository.findByCountryAndTopicId(
                getCountryOrDefault(country),
                topicId,
                pageable
        );
        return messages.map(messageMapper::toResponse);
    }

    @Override
    public MessageResponseTo getMessage(String country, Long topicId, Long messageId) {
        Message message = messageRepository.findByCountryAndTopicIdAndId(
                getCountryOrDefault(country),
                topicId,
                messageId
        );

        if (message == null) {
            throw new ResourceNotFoundException(
                    String.format("Message not found with country=%s, topicId=%d, id=%d",
                            country, topicId, messageId)
            );
        }

        return messageMapper.toResponse(message);
    }

    @Override
    public MessageResponseTo createMessage(String country, MessageRequestTo request) {
        // Генерируем ID
        Long messageId = idGenerator.getNextId();

        // Создаем сообщение
        Message message = messageMapper.toEntity(
                request,
                getCountryOrDefault(country),
                messageId
        );

        // Сохраняем в Cassandra
        Message savedMessage = messageRepository.save(message);

        return messageMapper.toResponse(savedMessage);
    }

    @Override
    public MessageResponseTo updateMessage(String country, Long topicId, Long messageId, MessageRequestTo request) {
        // Проверяем существование сообщения
        Message existingMessage = messageRepository.findByCountryAndTopicIdAndId(
                getCountryOrDefault(country),
                topicId,
                messageId
        );

        if (existingMessage == null) {
            throw new ResourceNotFoundException(
                    String.format("Message not found with country=%s, topicId=%d, id=%d",
                            country, topicId, messageId)
            );
        }

        // Обновляем содержимое
        existingMessage.setContent(request.getContent());
        existingMessage.setModified(java.time.LocalDateTime.now());

        // Сохраняем обновленное сообщение
        Message updatedMessage = messageRepository.save(existingMessage);

        return messageMapper.toResponse(updatedMessage);
    }

    @Override
    public void deleteMessage(String country, Long topicId, Long messageId) {
        if (!existsMessage(country, topicId, messageId)) {
            throw new ResourceNotFoundException(
                    String.format("Message not found with country=%s, topicId=%d, id=%d",
                            country, topicId, messageId)
            );
        }

        messageRepository.deleteByCountryAndTopicIdAndId(
                getCountryOrDefault(country),
                topicId,
                messageId
        );
    }

    @Override
    public boolean existsMessage(String country, Long topicId, Long messageId) {
        return messageRepository.existsByCountryAndTopicIdAndId(
                getCountryOrDefault(country),
                topicId,
                messageId
        );
    }

    private String getCountryOrDefault(String country) {
        return (country == null || country.trim().isEmpty()) ? DEFAULT_COUNTRY : country.trim();
    }

}