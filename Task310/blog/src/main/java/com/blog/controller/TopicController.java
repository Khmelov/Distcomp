package com.blog.controller;

import com.blog.dto.request.TopicRequestTo;
import com.blog.dto.response.TopicResponseTo;
import com.blog.service.TopicService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/v1.0/topics")
public class TopicController {

    @Autowired
    private TopicService topicService;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<TopicResponseTo> getAllTopics() {
        return topicService.getAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<TopicResponseTo> getTopicById(@PathVariable Long id) {
        TopicResponseTo topic = topicService.getById(id);
        return ResponseEntity.ok(topic);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public TopicResponseTo createTopic(@Valid @RequestBody TopicRequestTo request) {
        return topicService.create(request);
    }

    @PutMapping("/{id}")
    public ResponseEntity<TopicResponseTo> updateTopic(@PathVariable Long id,
                                                       @Valid @RequestBody TopicRequestTo request) {
        TopicResponseTo updatedTopic = topicService.update(id, request);
        return ResponseEntity.ok(updatedTopic);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteTopic(@PathVariable Long id) {
        topicService.delete(id);
    }

    @GetMapping("/editor/{editorId}")
    @ResponseStatus(HttpStatus.OK)
    public List<TopicResponseTo> getTopicsByEditorId(@PathVariable Long editorId) {
        return topicService.getByEditorId(editorId);
    }

    @GetMapping("/tag/{tagId}")
    @ResponseStatus(HttpStatus.OK)
    public List<TopicResponseTo> getTopicsByTagId(@PathVariable Long tagId) {
        return topicService.getByTagId(tagId);
    }

    // Альтернативный эндпоинт для совместимости
    @GetMapping("/writer/{writerId}")
    @ResponseStatus(HttpStatus.OK)
    public List<TopicResponseTo> getTopicsByWriterId(@PathVariable Long writerId) {
        return topicService.getByEditorId(writerId);
    }
}