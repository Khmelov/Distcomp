package com.blog.controller;

import com.blog.dto.request.MessageRequestTo;
import com.blog.dto.response.MessageResponseTo;
import com.blog.dto.response.TopicResponseTo;
import com.blog.service.MessageService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1.0/messages")
public class MessageController {

    @Autowired
    private MessageService messageService;

    @GetMapping
    public ResponseEntity<List<MessageResponseTo>> getAllMessage(){
        List<MessageResponseTo> message = messageService.getAll();
        return ResponseEntity.ok(message);
    }

    @GetMapping("/list")
    @ResponseStatus(HttpStatus.OK)
    public List<MessageResponseTo> getAllMessagesList() {
        return messageService.getAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<MessageResponseTo> getMessageById(@PathVariable Long id) {
        MessageResponseTo message = messageService.getById(id);
        return ResponseEntity.ok(message);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public MessageResponseTo createMessage(@Valid @RequestBody MessageRequestTo request) {
        return messageService.create(request);
    }

    @PutMapping("/{id}")
    public ResponseEntity<MessageResponseTo> updateMessage(@PathVariable Long id,
                                                           @Valid @RequestBody MessageRequestTo request) {
        MessageResponseTo updatedMessage = messageService.update(id, request);
        return ResponseEntity.ok(updatedMessage);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteMessage(@PathVariable Long id) {
        messageService.delete(id);
    }

    @GetMapping("/topic/{topicId}")
    @ResponseStatus(HttpStatus.OK)
    public Page<MessageResponseTo> getMessagesByTopicId(
            @PathVariable Long topicId,
            @PageableDefault(size = 10, sort = "created", direction = Sort.Direction.ASC) Pageable pageable) {
        return messageService.getByTopicId(topicId, pageable);
    }

    @GetMapping("/topic/{topicId}/list")
    @ResponseStatus(HttpStatus.OK)
    public List<MessageResponseTo> getMessagesByTopicIdList(@PathVariable Long topicId) {
        return messageService.getByTopicId(topicId);
    }
}