package org.example.task330.discussion.controller;

import jakarta.validation.Valid;
import java.util.List;
import org.example.task330.discussion.dto.MessageRequestTo;
import org.example.task330.discussion.dto.MessageResponseTo;
import org.example.task330.discussion.service.MessageService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1.0/messages")
public class MessageController {

    private final MessageService service;

    public MessageController(MessageService service) {
        this.service = service;
    }

    @GetMapping
    public List<MessageResponseTo> getAll(@RequestParam(required = false) String country,
                                          @RequestParam(required = false) Long tweetId) {
        if (country != null && tweetId != null) {
            return service.getAllByTweetId(country, tweetId);
        }
        return service.getAll();
    }

    @GetMapping("/{country}/{tweetId}/{id}")
    public MessageResponseTo getById(@PathVariable("country") String country,
                                     @PathVariable("tweetId") Long tweetId,
                                     @PathVariable("id") Long id) {
        return service.getById(country, tweetId, id);
    }

    // Legacy format support: GET /api/v1.0/messages/{id}
    @GetMapping("/{id}")
    public MessageResponseTo getByIdLegacy(@PathVariable("id") Long id) {
        // Try to find message by searching in all messages
        List<MessageResponseTo> allMessages = service.getAll();
        
        return allMessages.stream()
                .filter(msg -> msg.getId() != null && msg.getId().equals(id))
                .findFirst()
                .orElseThrow(() -> new org.example.task330.discussion.exception.NotFoundException("Message not found: " + id));
    }

    @PostMapping
    public ResponseEntity<MessageResponseTo> create(@Valid @RequestBody MessageRequestTo request) {
        MessageResponseTo response = service.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/{country}/{tweetId}/{id}")
    public MessageResponseTo update(@PathVariable("country") String country,
                                    @PathVariable("tweetId") Long tweetId,
                                    @PathVariable("id") Long id,
                                    @Valid @RequestBody MessageRequestTo request) {
        return service.update(country, tweetId, id, request);
    }

    // Legacy format support: PUT /api/v1.0/messages/{id}
    @PutMapping("/{id}")
    public MessageResponseTo updateLegacy(@PathVariable("id") Long id,
                                          @Valid @RequestBody MessageRequestTo request) {
        // Find message first to get country and tweetId
        List<MessageResponseTo> allMessages = service.getAll();
        MessageResponseTo found = allMessages.stream()
                .filter(msg -> msg.getId() != null && msg.getId().equals(id))
                .findFirst()
                .orElseThrow(() -> new org.example.task330.discussion.exception.NotFoundException("Message not found: " + id));
        
        String country = found.getCountry();
        Long tweetId = found.getTweetId();
        
        return service.update(country, tweetId, id, request);
    }

    @DeleteMapping("/{country}/{tweetId}/{id}")
    public ResponseEntity<Void> delete(@PathVariable("country") String country,
                                        @PathVariable("tweetId") Long tweetId,
                                        @PathVariable("id") Long id) {
        service.delete(country, tweetId, id);
        return ResponseEntity.noContent().build();
    }

    // Legacy format support: DELETE /api/v1.0/messages/{id}
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteLegacy(@PathVariable("id") Long id) {
        // Find message first to get country and tweetId
        List<MessageResponseTo> allMessages = service.getAll();
        MessageResponseTo found = allMessages.stream()
                .filter(msg -> msg.getId() != null && msg.getId().equals(id))
                .findFirst()
                .orElseThrow(() -> new org.example.task330.discussion.exception.NotFoundException("Message not found: " + id));
        
        String country = found.getCountry();
        Long tweetId = found.getTweetId();
        
        service.delete(country, tweetId, id);
        return ResponseEntity.noContent().build();
    }
}

