package com.blog.controller.v2;

import com.blog.config.SecurityUtils;
import com.blog.dto.request.MessageRequestTo;
import com.blog.dto.response.MessageResponseTo;
import com.blog.service.MessageService;
import com.blog.service.KafkaMessageProducer;
import com.blog.client.DiscussionClient;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v2.0/messages")
public class MessageControllerV2 {

    private static final Logger logger = LoggerFactory.getLogger(MessageControllerV2.class);

    private final MessageService messageService;
    private final KafkaMessageProducer kafkaMessageProducer;
    private final DiscussionClient discussionClient;

    @Autowired
    public MessageControllerV2(MessageService messageService,
                               KafkaMessageProducer kafkaMessageProducer,
                               DiscussionClient discussionClient) {
        this.messageService = messageService;
        this.kafkaMessageProducer = kafkaMessageProducer;
        this.discussionClient = discussionClient;
    }

    // Получить все сообщения (чтение доступно всем аутентифицированным)
    @GetMapping
    public ResponseEntity<List<MessageResponseTo>> getAllMessages() {
        logger.info("GET /api/v2.0/messages - Getting all messages");
        try {
            List<MessageResponseTo> messages = messageService.getAllMessages();
            return ResponseEntity.ok(messages);
        } catch (Exception e) {
            logger.error("Error getting all messages: {}", e.getMessage());
            return ResponseEntity.ok(List.of());
        }
    }

    // Получить сообщение по ID (чтение доступно всем аутентифицированным)
    @GetMapping("/{id}")
    public ResponseEntity<MessageResponseTo> getMessageById(@PathVariable("id") Long id) {
        logger.info("GET /api/v2.0/messages/{}", id);

        MessageResponseTo message = messageService.getMessageById(id);

        if ("NOT_FOUND".equals(message.getState())) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(message);
        }

        return ResponseEntity.ok(message);
    }

    // Создать сообщение (доступно всем аутентифицированным)
    @PostMapping
    public ResponseEntity<MessageResponseTo> createMessage(@Valid @RequestBody MessageRequestTo request) {
        logger.info("POST /api/v2.0/messages - Creating new message");

        try {
            // Валидация обязательных полей
            if (request.getTopicId() == null) {
                return ResponseEntity.badRequest().body(null);
            }
            if (request.getContent() == null || request.getContent().trim().isEmpty()) {
                return ResponseEntity.badRequest().body(null);
            }

            // Устанавливаем editorId текущего пользователя
            Long currentUserId = SecurityUtils.getCurrentUserId();
            if (currentUserId != null) {
                request.setEditorId(currentUserId);
            } else {
                request.setEditorId(1L); // fallback
            }

            // Устанавливаем значения по умолчанию
            if (request.getCountry() == null) {
                request.setCountry("global");
            }

            // 1. Создаем временное сообщение в локальной БД
            MessageResponseTo savedMessage = messageService.createMessage(request);

            // 2. Отправляем в Kafka для discussion service
            logger.info("Sending CREATE message to Kafka: ID={}", savedMessage.getId());
            kafkaMessageProducer.sendMessage(request);

            logger.info("Message created with ID: {}", savedMessage.getId());
            return ResponseEntity.status(HttpStatus.CREATED).body(savedMessage);

        } catch (Exception e) {
            logger.error("Error creating message: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    // Обновить сообщение
    // ADMIN может обновить любое, CUSTOMER только свое
    @PutMapping("/{id}")
    public ResponseEntity<MessageResponseTo> updateMessage(
            @PathVariable("id") Long id,
            @Valid @RequestBody MessageRequestTo request) {

        // Проверка аутентификации
        if (!SecurityUtils.isAuthenticated()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        // Проверка прав доступа
        Long currentUserId = SecurityUtils.getCurrentUserId();
        if (!SecurityUtils.isAdmin() &&
                !messageService.isMessageOwner(id, currentUserId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        logger.info("PUT /api/v2.0/messages/{}", id);

        try {
            // Проверяем существует ли сообщение
            if (!messageService.existsMessage(id)) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
            }

            // 1. Обновляем локально
            MessageResponseTo updatedMessage = messageService.updateMessage(id, request);

            // 2. Отправляем в Kafka для discussion service
            logger.info("Sending UPDATE message to Kafka: ID={}", id);
            kafkaMessageProducer.sendMessage(request);

            return ResponseEntity.ok(updatedMessage);

        } catch (RuntimeException e) {
            logger.error("Error updating message ID {}: {}", id, e.getMessage());

            if (e.getMessage().contains("Cannot edit message")) {
                MessageResponseTo error = new MessageResponseTo();
                error.setId(id);
                error.setContent("Cannot edit moderated message");
                error.setState("CONFLICT");
                return ResponseEntity.status(HttpStatus.CONFLICT).body(error);
            }

            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } catch (Exception e) {
            logger.error("Error updating message ID {}: {}", id, e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    // Удалить сообщение
    // ADMIN может удалить любое, CUSTOMER только свое
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or @messageSecurityService.isMessageOwner(#id, authentication)")
    public ResponseEntity<Void> deleteMessage(@PathVariable("id") Long id) {
        logger.info("DELETE /api/v2.0/messages/{}", id);

        try {
            // 1. Получаем текущее сообщение, чтобы знать его topicId
            MessageResponseTo currentMessage = messageService.getMessageById(id);
            if (currentMessage == null || "NOT_FOUND".equals(currentMessage.getState())) {
                return ResponseEntity.noContent().build();
            }

            // 2. Создаем DELETE запрос с ПРАВИЛЬНЫМИ данными
            MessageRequestTo deleteRequest = new MessageRequestTo();
            deleteRequest.setId(id);
            deleteRequest.setState("DELETED");
            deleteRequest.setTopicId(currentMessage.getTopicId());
            deleteRequest.setContent("[DELETED]");
            deleteRequest.setEditorId(currentMessage.getEditorId());
            deleteRequest.setCountry(currentMessage.getCountry());

            // 3. Отправляем DELETE команду в Kafka
            logger.info("Sending DELETE message to Kafka: ID={}, TopicId={}",
                    id, deleteRequest.getTopicId());
            kafkaMessageProducer.sendMessage(deleteRequest);

            // 4. Помечаем локальную копию как удаленную
            messageService.deleteMessage(id);

            return ResponseEntity.noContent().build();

        } catch (Exception e) {
            logger.error("Error deleting message ID {}: {}", id, e.getMessage());
            return ResponseEntity.noContent().build();
        }
    }

    // Получить сообщения текущего пользователя
    @GetMapping("/my-messages")
    public ResponseEntity<List<MessageResponseTo>> getMyMessages() {
        Long currentUserId = SecurityUtils.getCurrentUserId();
        if (currentUserId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        List<MessageResponseTo> messages = messageService.getMessagesByEditorId(currentUserId);
        return ResponseEntity.ok(messages);
    }

    // Получить сообщения текущего пользователя с пагинацией
    @GetMapping("/my-messages/paged")
    public ResponseEntity<Page<MessageResponseTo>> getMyMessagesPaged(
            @PageableDefault(size = 10, sort = "created", direction = Sort.Direction.DESC) Pageable pageable) {
        Long currentUserId = SecurityUtils.getCurrentUserId();
        if (currentUserId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        Page<MessageResponseTo> messages = messageService.getMessagesByEditorId(currentUserId, pageable);
        return ResponseEntity.ok(messages);
    }
}