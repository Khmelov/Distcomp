package com.blog.service;

import com.blog.client.DiscussionClient;
import com.blog.dto.request.MessageRequestTo;
import com.blog.dto.response.MessageResponseFromDiscussion;
import com.blog.dto.response.MessageResponseTo;
import com.blog.model.Message;
import com.blog.repository.MessageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class MessageService {

    @Autowired
    private MessageRepository messageRepository;

    @Autowired
    private DiscussionClient discussionClient;

    @Transactional(readOnly = true)
    public List<MessageResponseTo> getAllMessages() {
        List<Message> messages = messageRepository.findAll();
        return messages.stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public MessageResponseTo getMessageById(Long id) {
        // Сначала проверяем локально
        Optional<Message> localMessage = messageRepository.findById(id);

        if (localMessage.isPresent()) {
            Message message = localMessage.get();
            // Если сообщение в состоянии PENDING, пробуем синхронизировать с Discussion
            if ("PENDING".equals(message.getState())) {
                try {
                    MessageResponseTo synced = discussionClient.getMessage(id);
                    if (synced != null && !"PENDING".equals(synced.getState())) {
                        // Обновляем локальную копию
                        updateFromResponse(message, synced);
                        messageRepository.save(message);
                        return toResponse(message);
                    }
                } catch (Exception e) {
                    // Если не удалось синхронизировать, возвращаем локальную версию
                    System.err.println("Sync failed for message " + id + ": " + e.getMessage());
                }
            }
            return toResponse(message);
        } else {
            // Если локально нет, пытаемся получить из Discussion
            try {
                MessageResponseTo fromDiscussion = discussionClient.getMessage(id);
                if (fromDiscussion != null && !"NOT_FOUND".equals(fromDiscussion.getState())) {
                    // Сохраняем локально для будущих запросов
                    Message newMessage = createMessageFromResponse(fromDiscussion);
                    messageRepository.save(newMessage);
                    return fromDiscussion;
                }
            } catch (Exception e) {
                // Если Discussion не ответил, возвращаем NOT_FOUND
                System.err.println("Failed to fetch from discussion for id " + id + ": " + e.getMessage());
            }

            MessageResponseTo notFoundResponse = new MessageResponseTo();
            notFoundResponse.setId(id);
            notFoundResponse.setState("NOT_FOUND");
            notFoundResponse.setContent("Message not found");
            notFoundResponse.setApproved(false);
            notFoundResponse.setDeclined(false);
            notFoundResponse.setPending(false);
            return notFoundResponse;
        }
    }

    @Transactional
    public MessageResponseTo createMessage(MessageRequestTo request) {
        // Генерируем временный ID
        Long tempId = generateMessageId();

        // Создаем временное сообщение со статусом PENDING
        Message message = new Message();
        message.setId(tempId);
        message.setTopicId(request.getTopicId());
        message.setContent(request.getContent());
        message.setEditorId(request.getEditorId() != null ? request.getEditorId() : 1L);
        message.setCountry(request.getCountry() != null ? request.getCountry() : "global");
        message.setState("PENDING");
        message.setCreated(LocalDateTime.now());
        message.setModified(LocalDateTime.now());

        message = messageRepository.save(message);

        // Устанавливаем ID обратно в request для Kafka
        request.setId(tempId);
        request.setState("PENDING");

        return toResponse(message);
    }

    @Transactional
    public MessageResponseTo updateMessage(Long id, MessageRequestTo request) {
        // Проверяем существует ли сообщение
        Optional<Message> existing = messageRepository.findById(id);

        if (existing.isEmpty()) {
            throw new RuntimeException("Message not found with id: " + id);
        }

        Message message = existing.get();

        // Синхронизируем статус с discussion перед обновлением
        try {
            MessageResponseTo fromDiscussion = discussionClient.getMessage(id);
            if (fromDiscussion != null && !"NOT_FOUND".equals(fromDiscussion.getState())) {
                // Обновляем локальный статус
                message.setState(fromDiscussion.getState());
            }
        } catch (Exception e) {
            // Если не удалось синхронизировать, продолжаем с текущим статусом
            System.err.println("Sync failed for message " + id + ": " + e.getMessage());
        }

        // Обновляем локальную копию
        message.setContent(request.getContent());
        if (request.getEditorId() != null) {
            message.setEditorId(request.getEditorId());
        }
        if (request.getCountry() != null) {
            message.setCountry(request.getCountry());
        }
        message.setState("PENDING"); // Сбрасываем статус при обновлении
        message.setModified(LocalDateTime.now());

        message = messageRepository.save(message);

        // Обновляем request для Kafka
        request.setId(id);
        request.setState("PENDING");

        return toResponse(message);
    }

    @Transactional
    public MessageResponseTo syncMessageFromDiscussion(MessageResponseFromDiscussion response) {
        if (response.getId() == null) {
            throw new RuntimeException("Cannot sync message without ID");
        }

        Optional<Message> existingOpt = messageRepository.findById(response.getId());

        if (existingOpt.isPresent()) {
            Message message = existingOpt.get();
            updateFromDiscussionResponse(message, response);
            message = messageRepository.save(message);
            return toResponse(message);
        } else {
            Message newMessage = createMessageFromDiscussionResponse(response);
            newMessage = messageRepository.save(newMessage);
            return toResponse(newMessage);
        }
    }

    @Transactional
    public void deleteMessage(Long id) {
        // Не удаляем физически, а помечаем как удаленное
        Optional<Message> messageOpt = messageRepository.findById(id);
        if (messageOpt.isPresent()) {
            Message message = messageOpt.get();
            message.setState("DELETED");
            message.setModified(LocalDateTime.now());
            messageRepository.save(message);
        }
    }

    @Transactional(readOnly = true)
    public boolean existsMessage(Long id) {
        Optional<Message> message = messageRepository.findById(id);
        return message.isPresent() && !"DELETED".equals(message.get().getState());
    }

    private Long generateMessageId() {
        // Используем временную метку для уникальности
        return System.currentTimeMillis();
    }

    private void updateFromDiscussionResponse(Message message, MessageResponseFromDiscussion response) {
        if (response.getTopicId() != null) {
            message.setTopicId(response.getTopicId());
        }
        if (response.getContent() != null) {
            message.setContent(response.getContent());
        }
        if (response.getEditorId() != null) {
            message.setEditorId(response.getEditorId());
        }
        if (response.getCountry() != null) {
            message.setCountry(response.getCountry());
        }
        // Используем нормализованное состояние из response
        message.setState(response.getNormalizedState());
        if (response.getModified() != null) {
            message.setModified(response.getModified());
        } else {
            message.setModified(LocalDateTime.now());
        }
    }

    private void updateFromResponse(Message message, MessageResponseTo response) {
        if (response.getTopicId() != null) {
            message.setTopicId(response.getTopicId());
        }
        if (response.getContent() != null) {
            message.setContent(response.getContent());
        }
        if (response.getEditorId() != null) {
            message.setEditorId(response.getEditorId());
        }
        if (response.getCountry() != null) {
            message.setCountry(response.getCountry());
        }
        message.setState(response.getState());
        message.setModified(LocalDateTime.now());
    }

    private Message createMessageFromResponse(MessageResponseTo response) {
        Message message = new Message();
        message.setId(response.getId());
        message.setTopicId(response.getTopicId());
        message.setContent(response.getContent());
        message.setEditorId(response.getEditorId());
        message.setCountry(response.getCountry());
        message.setState(response.getState() != null ? response.getState() : "PENDING");
        message.setCreated(response.getCreated() != null ? response.getCreated() : LocalDateTime.now());
        message.setModified(LocalDateTime.now());
        return message;
    }

    private Message createMessageFromDiscussionResponse(MessageResponseFromDiscussion response) {
        Message message = new Message();
        message.setId(response.getId());
        message.setTopicId(response.getTopicId());
        message.setContent(response.getContent());
        message.setEditorId(response.getEditorId());
        message.setCountry(response.getCountry());
        message.setState(response.getNormalizedState());
        message.setCreated(response.getCreated() != null ? response.getCreated() : LocalDateTime.now());
        message.setModified(LocalDateTime.now());
        return message;
    }

    private MessageResponseTo toResponse(Message message) {
        MessageResponseTo response = new MessageResponseTo();
        response.setId(message.getId());
        response.setTopicId(message.getTopicId());
        response.setContent(message.getContent());
        response.setEditorId(message.getEditorId());
        response.setCountry(message.getCountry());
        response.setState(message.getState());
        response.setCreated(message.getCreated());
        response.setModified(message.getModified());

        // Устанавливаем boolean поля на основе состояния
        if (message.getState() != null) {
            response.setApproved("APPROVED".equals(message.getState()) || "APPROVE".equals(message.getState()));
            response.setDeclined("DECLINED".equals(message.getState()) || "DECLINE".equals(message.getState()));
            response.setPending("PENDING".equals(message.getState()));
        }

        return response;
    }
}