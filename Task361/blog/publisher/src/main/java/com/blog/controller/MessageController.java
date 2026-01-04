package com.blog.controller;

import com.blog.client.DiscussionClient;
import com.blog.dto.request.MessageRequestTo;
import com.blog.dto.response.MessageResponseTo;
import com.blog.service.KafkaMessageProducer;
import com.blog.service.MessageService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1.0/messages")
public class MessageController {

    private static final Logger logger = LoggerFactory.getLogger(MessageController.class);

    private final DiscussionClient discussionClient;
    private final KafkaMessageProducer kafkaMessageProducer;
    private final MessageService messageService;

    @Autowired
    public MessageController(DiscussionClient discussionClient,
                             KafkaMessageProducer kafkaMessageProducer,
                             MessageService messageService) {
        this.discussionClient = discussionClient;
        this.kafkaMessageProducer = kafkaMessageProducer;
        this.messageService = messageService;
    }

    @GetMapping
    public ResponseEntity<List<MessageResponseTo>> getAllMessages() {
        logger.info("GET /api/v1.0/messages - Getting all messages");
        try {
            List<MessageResponseTo> messages = messageService.getAllMessages();
            return ResponseEntity.ok(messages);
        } catch (Exception e) {
            logger.error("Error getting all messages: {}", e.getMessage());
            return ResponseEntity.ok(List.of());
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<MessageResponseTo> getMessageById(@PathVariable("id") Long id) {
        logger.info("GET /api/v1.0/messages/{}", id);

        MessageResponseTo message = messageService.getMessageById(id);

        // Проверяем, является ли это сообщением "NOT_FOUND"
        if ("NOT_FOUND".equals(message.getState())) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(message);
        }

        return ResponseEntity.ok(message);
    }

    @PostMapping
    public ResponseEntity<MessageResponseTo> createMessage(@RequestBody MessageRequestTo request) {
        logger.info("POST /api/v1.0/messages - Creating new message");

        try {
            // Валидация обязательных полей
            if (request.getTopicId() == null) {
                return ResponseEntity.badRequest().body(null);
            }
            if (request.getContent() == null || request.getContent().trim().isEmpty()) {
                return ResponseEntity.badRequest().body(null);
            }

            // Устанавливаем значения по умолчанию
            if (request.getEditorId() == null) {
                request.setEditorId(1L);
            }
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

    @PutMapping("/{id}")
    public ResponseEntity<MessageResponseTo> updateMessage(
            @PathVariable("id") Long id,
            @RequestBody MessageRequestTo request) {

        logger.info("PUT /api/v1.0/messages/{}", id);

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

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteMessage(@PathVariable("id") Long id) {
        logger.info("DELETE /api/v1.0/messages/{}", id);

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
}