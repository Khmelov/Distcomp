package com.example.Labs.controller;

import com.example.Labs.client.MessageKafkaClient;
import com.example.Labs.repository.StoryRepository;
import com.example.Labs.dto.request.MessageRequestTo;
import com.example.Labs.dto.response.MessageResponseTo;
import com.example.Labs.dto.response.ErrorResponse;
import com.example.Labs.exception.ResourceNotFoundException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1.0/messages")
@RequiredArgsConstructor
public class MessageProxyController {
    private final MessageKafkaClient kafkaClient;
    private final StoryRepository storyRepository;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public MessageResponseTo create(@Valid @RequestBody MessageRequestTo request) {
        if (!storyRepository.existsById(request.getStoryId())) {
            throw new IllegalArgumentException("Story not found");
        }
        return kafkaClient.sendAndReceive("CREATE", null, request);
    }

    @GetMapping
    public List<MessageResponseTo> getAll() {
        return kafkaClient.getAll();
    }

    @GetMapping("/{id}")
    public MessageResponseTo getById(@PathVariable Long id) {
        return kafkaClient.sendAndReceive("GET_BY_ID", id, null);
    }

    @PutMapping
    public MessageResponseTo updateFromBody(@RequestBody Map<String, Object> body) {
        Long id = Long.valueOf(body.get("id").toString());
        MessageRequestTo req = new MessageRequestTo();
        req.setStoryId(Long.valueOf(body.get("storyId").toString()));
        req.setContent(body.get("content").toString());
        return kafkaClient.sendAndReceive("UPDATE", id, req);
    }

    @PutMapping("/{id}")
    public MessageResponseTo update(@PathVariable Long id, @Valid @RequestBody MessageRequestTo request) {
        return kafkaClient.sendAndReceive("UPDATE", id, request);
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        kafkaClient.deleteViaKafka(id); // Вызываем метод с аннотацией @CacheEvict
        return ResponseEntity.noContent().build();
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ErrorResponse> handleTypeMismatch(MethodArgumentTypeMismatchException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ErrorResponse("40001", "Invalid format"));
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleNotFound(ResourceNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new ErrorResponse("40401", ex.getMessage()));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleError(Exception ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ErrorResponse("40001", ex.getMessage()));
    }
}