package com.example.demo.controllers.v2;

import com.example.demo.dto.requests.MessageKafkaRequestTo;
import com.example.demo.dto.requests.MessageRequestTo;
import com.example.demo.dto.responses.MessageResponseTo;
import com.example.demo.producer.MessageKafkaProducer;
import com.example.demo.service.MessageClientService;
import jakarta.validation.Valid;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v2.0/messages")
public class MessageControllerV2 {

    private final MessageClientService messageService;
    private final MessageKafkaProducer messageKafkaProducer;

    public MessageControllerV2(MessageClientService messageService, MessageKafkaProducer messageKafkaProducer) {
        this.messageService = messageService;
        this.messageKafkaProducer = messageKafkaProducer;
    }

    @PostMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<MessageResponseTo> create(@Valid @RequestBody MessageRequestTo dto) {
        Long id = System.nanoTime();
        MessageKafkaRequestTo kafkaRequest = new MessageKafkaRequestTo(id, dto.getIssueId(), dto.getContent());
        messageKafkaProducer.send(kafkaRequest);
        MessageResponseTo response = new MessageResponseTo(id, dto.getIssueId(), dto.getContent(), "PENDING");
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<MessageResponseTo>> findAll(
            @RequestParam(name = "page", required = false) Integer page,
            @RequestParam(name = "size", required = false) Integer size,
            @RequestParam(name = "sort", defaultValue = "id,asc") String sort,
            @RequestParam(name = "content", required = false) String content,
            @RequestParam(name = "issueId", required = false) Long issueId) {
        Pageable pageable = (page != null && size != null) ? PageRequest.of(page, size, parseSort(sort)) : Pageable.unpaged();
        var pageResult = messageService.findAll(pageable, content, issueId);
        return ResponseEntity.ok(pageResult.getContent());
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or (hasRole('CUSTOMER') and @messageClientService.isOwnerOfMessage(#id, authentication.name))")
    public ResponseEntity<MessageResponseTo> findById(@PathVariable Long id) {
        return ResponseEntity.ok(messageService.findById(id));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or (hasRole('CUSTOMER') and @messageClientService.isOwnerOfMessage(#id, authentication.name))")
    public ResponseEntity<MessageResponseTo> update(@PathVariable("id") Long id, @Valid @RequestBody MessageRequestTo dto) {
        return ResponseEntity.ok(messageService.update(id, dto));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> delete(@PathVariable("id") Long id) {
        messageService.delete(id);
        return ResponseEntity.noContent().build();
    }

    private Sort parseSort(String sort) {
        String[] parts = sort.split(",");
        String field = parts[0];
        Sort.Direction direction = parts.length > 1 && "desc".equalsIgnoreCase(parts[1])
                ? Sort.Direction.DESC : Sort.Direction.ASC;
        return Sort.by(direction, field);
    }
}
