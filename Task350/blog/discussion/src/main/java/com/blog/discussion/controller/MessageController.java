package com.blog.discussion.controller;

import com.blog.discussion.dto.request.MessageRequestTo;
import com.blog.discussion.dto.response.MessageResponseTo;
import com.blog.discussion.exception.ResourceNotFoundException;
import com.blog.discussion.service.MessageService;
import com.blog.discussion.service.impl.MessageServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1.0/messages")
public class MessageController {

    @Autowired
    private MessageService messageService;

    @Autowired
    private MessageServiceImpl messageServiceImpl;

    @GetMapping
    public ResponseEntity<List<MessageResponseTo>> getAllMessages() {
        try {
            List<MessageResponseTo> messages = messageService.getAllMessages();
            messages.forEach(MessageResponseTo::setAllBooleanFields);
            return ResponseEntity.ok(messages);
        } catch (Exception e) {
            return ResponseEntity.ok(List.of());
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<MessageResponseTo> getMessage(@PathVariable("id") Long id) {
        try {
            // Сначала пробуем найти по ID без country и topicId
            Optional<MessageResponseTo> messageOpt = messageServiceImpl.getMessageById(id);

            if (messageOpt.isPresent()) {
                MessageResponseTo message = messageOpt.get();
                message.setAllBooleanFields();
                return ResponseEntity.ok(message);
            }

            // Если не нашли, пробуем стандартным способом
            MessageResponseTo message = messageService.getMessage("global", 1L, id);
            if (message != null) {
                message.setAllBooleanFields();
                return ResponseEntity.ok(message);
            } else {
                return createNotFoundResponse(id);
            }
        } catch (ResourceNotFoundException e) {
            return createNotFoundResponse(id);
        } catch (Exception e) {
            MessageResponseTo errorResponse = new MessageResponseTo();
            errorResponse.setId(id);
            errorResponse.setContent("Error retrieving message: " + e.getMessage());
            errorResponse.setState("ERROR");
            errorResponse.setApproved(false);
            errorResponse.setDeclined(false);
            errorResponse.setPending(false);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    @PostMapping
    public ResponseEntity<MessageResponseTo> createMessage(@RequestBody MessageRequestTo request) {
        try {
            String country = request.getCountry() != null ? request.getCountry() : "global";
            Long topicId = request.getTopicId() != null ? request.getTopicId() : 1L;

            MessageResponseTo createdMessage = messageService.createMessage(country, topicId, request);
            if (createdMessage != null) {
                createdMessage.setAllBooleanFields();
                return ResponseEntity.status(HttpStatus.CREATED).body(createdMessage);
            } else {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<MessageResponseTo> updateMessage(
            @PathVariable("id") Long id,
            @RequestBody MessageRequestTo request) {
        try {
            String country = request.getCountry() != null ? request.getCountry() : "global";
            Long topicId = request.getTopicId() != null ? request.getTopicId() : 1L;

            MessageResponseTo updatedMessage = messageService.updateMessage(country, topicId, id, request);
            if (updatedMessage != null) {
                updatedMessage.setAllBooleanFields();
                return ResponseEntity.ok(updatedMessage);
            } else {
                return createNotFoundResponse(id);
            }
        } catch (ResourceNotFoundException e) {
            return createNotFoundResponse(id);
        } catch (Exception e) {
            return createNotFoundResponse(id);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteMessage(@PathVariable("id") Long id) {
        try {
            messageService.deleteMessage("global", 1L, id);
            return ResponseEntity.noContent().build();
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    private ResponseEntity<MessageResponseTo> createNotFoundResponse(Long id) {
        MessageResponseTo errorResponse = new MessageResponseTo();
        errorResponse.setId(id);
        errorResponse.setContent("Message not found");
        errorResponse.setState("NOT_FOUND");
        errorResponse.setApproved(false);
        errorResponse.setDeclined(false);
        errorResponse.setPending(false);
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
    }
}

