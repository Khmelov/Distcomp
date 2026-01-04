package com.blog.service;

import com.blog.dto.request.MessageRequestTo;
import com.blog.dto.response.MessageResponseTo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface MessageServiceInterface {

    // Основные CRUD операции
    List<MessageResponseTo> getAllMessages();
    MessageResponseTo getMessageById(Long id);
    MessageResponseTo createMessage(MessageRequestTo request);
    MessageResponseTo updateMessage(Long id, MessageRequestTo request);
    void deleteMessage(Long id);
    boolean existsMessage(Long id);

    // Новые методы для поддержки безопасности
    List<MessageResponseTo> getMessagesByEditorId(Long editorId);
    Page<MessageResponseTo> getMessagesByEditorId(Long editorId, Pageable pageable);
    boolean isMessageOwner(Long messageId, Long editorId);

    // Методы для синхронизации
    MessageResponseTo syncMessageFromDiscussion(com.blog.dto.response.MessageResponseFromDiscussion response);
}