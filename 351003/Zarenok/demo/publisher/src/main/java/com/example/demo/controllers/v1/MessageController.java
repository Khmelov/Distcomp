package com.example.demo.controllers.v1;

import com.example.demo.dto.requests.MessageKafkaRequestTo;
import com.example.demo.dto.requests.MessageRequestTo;
import com.example.demo.dto.responses.MessageResponseTo;
import com.example.demo.producer.MessageKafkaProducer;
import com.example.demo.service.MessageClientService;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1.0/messages")
@Validated
public class MessageController {
    private final MessageClientService messageService;
    private final MessageKafkaProducer messageKafkaProducer;

    public MessageController(MessageClientService messageService, MessageKafkaProducer messageKafkaProducer) {
        this.messageService = messageService;
        this.messageKafkaProducer = messageKafkaProducer;
    }

    @PostMapping
    public ResponseEntity<MessageResponseTo> create(@Valid @RequestBody MessageRequestTo dto) {
        Long id = System.nanoTime();
        MessageKafkaRequestTo kafkaRequest = new MessageKafkaRequestTo(id, dto.getIssueId(), dto.getContent());
        messageKafkaProducer.send(kafkaRequest);

        MessageResponseTo response = new MessageResponseTo(id, dto.getIssueId(), dto.getContent(), "PENDING");
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }


    @GetMapping
    public ResponseEntity<List<MessageResponseTo>> findAll(
            @RequestParam(name = "page", required = false) Integer page,
            @RequestParam(name = "size", required = false) Integer size,
            @RequestParam(name = "sort", defaultValue = "id,asc") String sort,
            @RequestParam(name = "content", required = false) String content,
            @RequestParam(name = "issueId", required = false) Long issueId
    ) {
        Pageable pageable = (page != null && size != null) ? PageRequest.of(page, size, parseSort(sort)) : Pageable.unpaged();
        Page<MessageResponseTo> pageResult = messageService.findAll(pageable, content, issueId);
        return ResponseEntity.ok(pageResult.getContent());
    }

    private Sort parseSort(String sort) {
        String[] parts = sort.split(",");
        String field = parts[0];
        Sort.Direction direction = parts.length > 1 && "desc".equalsIgnoreCase(parts[1])
                ? Sort.Direction.DESC : Sort.Direction.ASC;
        return Sort.by(direction, field);
    }

    @GetMapping("/{id}")
    public ResponseEntity<MessageResponseTo> findById(@PathVariable("id") Long id) {
        MessageResponseTo message = messageService.findById(id);
        return ResponseEntity.ok(message);
    }

    @PutMapping("/{id}")
    public ResponseEntity<MessageResponseTo> update(@PathVariable("id") Long id,
                                                    @Valid @RequestBody MessageRequestTo dto) {
        MessageResponseTo updated = messageService.update(id, dto);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable("id") Long id) {
        messageService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
