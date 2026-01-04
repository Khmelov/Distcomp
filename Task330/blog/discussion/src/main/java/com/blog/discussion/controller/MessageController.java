package com.blog.discussion.controller;

import com.blog.discussion.dto.request.MessageRequestTo;
import com.blog.discussion.dto.response.MessageResponseTo;
import com.blog.discussion.exception.ResourceNotFoundException;
import com.blog.discussion.service.MessageService;
import com.blog.discussion.repository.MessageRepository;
import com.blog.discussion.model.Message;
import com.blog.discussion.mapper.MessageMapper;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1.0/messages")
public class MessageController {

    @Autowired
    private MessageService messageService;
    @Autowired
    private MessageRepository messageRepository;
    @Autowired
    private MessageMapper messageMapper;

    @GetMapping
    public ResponseEntity<List<MessageResponseTo>> getAllMessages() {
        List<MessageResponseTo> messages = messageService.getAllMessages();
        return ResponseEntity.ok(messages);
    }
    // Получить все сообщения для топика (по умолчанию страна "global")
    @GetMapping("/topic/{topicId}")
    public ResponseEntity<List<MessageResponseTo>> getMessagesByTopic(
            @PathVariable Long topicId,
            @RequestParam(required = false, defaultValue = "global") String country) {
        List<MessageResponseTo> messages = messageService.getMessagesByTopic(country, topicId);
        return ResponseEntity.ok(messages);
    }

    // Получить сообщения с пагинацией
    @GetMapping("/topic/{topicId}/page")
    public ResponseEntity<Slice<MessageResponseTo>> getMessagesByTopicPage(
            @PathVariable Long topicId,
            @RequestParam(required = false, defaultValue = "global") String country,
            @PageableDefault(size = 10, sort = "id", direction = Sort.Direction.ASC) Pageable pageable) {
        Slice<MessageResponseTo> messages = messageService.getMessagesByTopic(country, topicId, pageable);
        return ResponseEntity.ok(messages);
    }

    // Получить конкретное сообщение
    // В Discussion MessageController
    @GetMapping("/{id}")
    public ResponseEntity<?> getMessage(
            @PathVariable("id") Long id,
            @RequestParam(value = "topicId", required = false) Long topicId,
            @RequestParam(value = "country", required = false, defaultValue = "global") String country) {

        System.out.println("=== GET /messages/" + id + " ===");
        System.out.println("Params: country=" + country + ", topicId=" + topicId);

        try {
            // Случай 1: Если topicId указан, ищем точное совпадение
            if (topicId != null) {
                Message message = messageRepository.findByCountryAndTopicIdAndId(country, topicId, id);
                if (message != null) {
                    System.out.println("Found exact match: " + message);
                    return ResponseEntity.ok(messageMapper.toResponse(message));
                }
                System.out.println("No exact match found for topicId=" + topicId);
            }

            // Случай 2: Ищем любое сообщение с таким id для указанной страны
            System.out.println("Searching any message with id=" + id + " for country=" + country);

            List<Message> allMessages = messageRepository.findByCountry(country);
            System.out.println("Total messages for country '" + country + "': " + allMessages.size());

            Optional<Message> found = allMessages.stream()
                    .filter(m -> m.getId().equals(id))
                    .findFirst();

            if (found.isPresent()) {
                Message message = found.get();
                System.out.println("Found message: " + message);
                return ResponseEntity.ok(messageMapper.toResponse(message));
            }

            // Случай 3: Если не нашли, ищем в любой стране
            System.out.println("Not found for country=" + country + ", searching all countries...");

            // Получаем все сообщения (может потребоваться создать метод в репозитории)
            List<Message> allCountriesMessages = messageRepository.findAll();
            Optional<Message> anyCountryMessage = allCountriesMessages.stream()
                    .filter(m -> m.getId().equals(id))
                    .findFirst();

            if (anyCountryMessage.isPresent()) {
                Message message = anyCountryMessage.get();
                System.out.println("Found in another country: " + message);
                return ResponseEntity.ok(messageMapper.toResponse(message));
            }

            // Случай 4: Если вообще не нашли, возвращаем 404 с информацией
            System.out.println("Message with id=" + id + " not found in database");

            // Возвращаем информацию о том, какие данные есть
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", "Message not found with id: " + id);
            errorResponse.put("requestedId", id);
            errorResponse.put("requestedTopicId", topicId);
            errorResponse.put("requestedCountry", country);

            // Добавляем информацию о доступных сообщениях
                      errorResponse.put("totalAvailable", allMessages.size());

            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);

        } catch (Exception e) {
            System.err.println("Error getting message: " + e.getMessage());
            e.printStackTrace();

            Map<String, String> error = new HashMap<>();
            error.put("error", "Internal server error");
            error.put("message", e.getMessage());

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    // Создать новое сообщение
    @PostMapping
    public ResponseEntity<MessageResponseTo> createMessage(
            @RequestParam(required = false, defaultValue = "global") String country,
            @Valid @RequestBody MessageRequestTo request) {
        MessageResponseTo createdMessage = messageService.createMessage(country, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdMessage);
    }

    // Обновить существующее сообщение
    @PutMapping("/{id}")
    public ResponseEntity<MessageResponseTo> updateMessage(
            @PathVariable("id") Long id,
            @RequestParam(value = "topicId", required = false) Long topicIdParam,
            @RequestParam(value = "country", required = false, defaultValue = "global") String country,
            @Valid @RequestBody MessageRequestTo request) {

        System.out.println("=== PUT /messages/" + id + " ===");
        System.out.println("Query params - topicId: " + topicIdParam + ", country: " + country);
        System.out.println("Request body: " + request);

        try {
            // Используем topicId из параметра или из тела запроса
            Long actualTopicId = (topicIdParam != null) ? topicIdParam : request.getTopicId();

            if (actualTopicId == null) {
                return ResponseEntity.badRequest().body(null);
            }

            MessageResponseTo updated = messageService.updateMessage(country, actualTopicId, id, request);
            return ResponseEntity.ok(updated);

        } catch (ResourceNotFoundException e) {
            System.err.println("Message not found for update: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        } catch (Exception e) {
            System.err.println("Error updating message: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteMessage(
            @PathVariable("id") Long id,
            @RequestParam(value = "topicId", required = false) Long topicId,
            @RequestParam(value = "country", required = false, defaultValue = "global") String country) {

        System.out.println("=== DELETE /messages/" + id + " ===");
        System.out.println("Params: topicId=" + topicId + ", country=" + country);

        try {
            // Если topicId не указан, ищем сообщение чтобы удалить
            if (topicId == null) {
                List<Message> allMessages = messageRepository.findByCountry(country);
                Optional<Message> toDelete = allMessages.stream()
                        .filter(m -> m.getId().equals(id))
                        .findFirst();

                if (toDelete.isPresent()) {
                    Message message = toDelete.get();
                    messageRepository.deleteByCountryAndTopicIdAndId(
                            message.getCountry(),
                            message.getTopicId(),
                            message.getId()
                    );
                    System.out.println("Deleted message: " + message);
                    return ResponseEntity.noContent().build();
                }
            } else {
                // Удаляем по точным параметрам
                messageRepository.deleteByCountryAndTopicIdAndId(country, topicId, id);
                System.out.println("Deleted message with exact match");
                return ResponseEntity.noContent().build();
            }

            System.out.println("Message not found for deletion");
            return ResponseEntity.notFound().build();

        } catch (Exception e) {
            System.err.println("Error deleting message: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // Проверить существование сообщения
    @GetMapping("/{id}/exists")
    public ResponseEntity<Boolean> existsMessage(
            @PathVariable Long id,
            @RequestParam(value = "topicId", required = false, defaultValue = "1") Long topicId,
            @RequestParam(required = false, defaultValue = "global") String country) {
        boolean exists = messageService.existsMessage(country, topicId, id);
        return ResponseEntity.ok(exists);
    }
}