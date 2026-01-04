package com.blog.discussion.service;

import com.blog.discussion.dto.request.MessageRequestTo;
import com.blog.discussion.dto.response.MessageResponseTo;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

import java.util.List;
import java.util.Optional;

public interface MessageService {

    List<MessageResponseTo> getAllMessages();

    // Получить все сообщения для страны и топика
    List<MessageResponseTo> getMessagesByTopic(String country, Long topicId);

    // Получить сообщения с пагинацией
    Slice<MessageResponseTo> getMessagesByTopic(String country, Long topicId, Pageable pageable);

    // Получить конкретное сообщение
    MessageResponseTo getMessage(String country, Long topicId, Long messageId);

    // Создать сообщение
    MessageResponseTo createMessage(String country, Long topicId, MessageRequestTo request);

    // Обновить сообщение
    MessageResponseTo updateMessage(String country, Long topicId, Long messageId, MessageRequestTo request);

    // Удалить сообщение
    void deleteMessage(String country, Long topicId, Long messageId);

    // Проверить существование сообщения
    boolean existsMessage(String country, Long topicId, Long messageId);

    void processIncomingMessage(MessageRequestTo request);

    Optional<MessageResponseTo> getMessageById(Long id);
}

