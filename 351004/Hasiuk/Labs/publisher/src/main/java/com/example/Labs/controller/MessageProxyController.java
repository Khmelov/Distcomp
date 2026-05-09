package com.example.Labs.controller;
import com.example.Labs.client.MessageKafkaClient;
import com.example.Labs.dto.request.MessageRequestTo;
import com.example.Labs.dto.response.MessageResponseTo;
import com.example.Labs.entity.Story;
import com.example.Labs.repository.StoryRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping({"/api/v1.0/messages", "/api/v2.0/messages"})
@RequiredArgsConstructor
public class MessageProxyController {
    private final MessageKafkaClient kafkaClient;
    private final StoryRepository storyRepository;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public MessageResponseTo create(@Valid @RequestBody MessageRequestTo request) {
        if (isV2()) {
            Story story = storyRepository.findById(request.getStoryId()).orElseThrow();
            checkStoryOwnership(story);
        }
        return kafkaClient.sendAndReceive("CREATE", null, request);
    }

    @GetMapping
    public List<MessageResponseTo> getAll() {
        return kafkaClient.getAll();
    }

    @GetMapping("/{id}")
    public MessageResponseTo getById(@PathVariable Long id) {
        // Используем кешируемый метод — сначала проверяем Redis, потом Kafka
        return kafkaClient.getByIdCached(id);
    }

    @PutMapping("/{id}")
    public MessageResponseTo update(@PathVariable Long id, @Valid @RequestBody MessageRequestTo request) {
        if (isV2()) {
            MessageResponseTo existing = kafkaClient.getByIdCached(id);
            Story story = storyRepository.findById(existing.getStoryId()).orElseThrow();
            checkStoryOwnership(story);
        }
        // CacheEvict + Kafka update
        return kafkaClient.updateViaKafka(id, request);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        if (isV2()) {
            MessageResponseTo existing = kafkaClient.getByIdCached(id);
            Story story = storyRepository.findById(existing.getStoryId()).orElseThrow();
            checkStoryOwnership(story);
        }
        kafkaClient.deleteViaKafka(id);
        return ResponseEntity.noContent().build();
    }

    private boolean isV2() {
        var auth = SecurityContextHolder.getContext().getAuthentication();
        return auth != null && !auth.getPrincipal().equals("anonymousUser");
    }

    private void checkStoryOwnership(Story story) {
        String currentLogin = SecurityContextHolder.getContext().getAuthentication().getName();
        boolean isAdmin = SecurityContextHolder.getContext().getAuthentication().getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
        if (!isAdmin && !story.getEditor().getLogin().equals(currentLogin)) {
            throw new RuntimeException("403 Forbidden");
        }
    }
}