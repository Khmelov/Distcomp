package com.group310971.gormash.controller;

import com.group310971.gormash.dto.TopicRequestTo;
import com.group310971.gormash.dto.TopicResponseTo;
import com.group310971.gormash.service.TopicService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1.0/topics")
class TopicController {
    private final TopicService topicService;

    @PostMapping
    public ResponseEntity<TopicResponseTo> createTopic(@Valid @RequestBody TopicRequestTo topicRequestTo) {
        try {
            TopicResponseTo createdTopic = topicService.createTopic(topicRequestTo);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdTopic);
        } catch(Exception e){
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new TopicResponseTo());
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<TopicResponseTo> updateTopic(@PathVariable Long id, @Valid @RequestBody TopicRequestTo topicRequestTo) {
        TopicResponseTo updatedTopic = topicService.updateTopic(id, topicRequestTo);
        return ResponseEntity.ok(updatedTopic);
    }

    @GetMapping
    public ResponseEntity<List<TopicResponseTo>> getAllTopics() {
        List<TopicResponseTo> topics = topicService.getAllTopics();
        return ResponseEntity.ok(topics);
    }

    @GetMapping("/{id}")
    public ResponseEntity<TopicResponseTo> getTopicById(@PathVariable Long id) {
        TopicResponseTo topic = topicService.getTopicById(id);
        return ResponseEntity.ok(topic);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<TopicResponseTo> deleteTopic(@PathVariable Long id) {
        try {
            TopicResponseTo deleted = topicService.deleteTopic(id);
            return new ResponseEntity<>(deleted, HttpStatus.NO_CONTENT);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
}
