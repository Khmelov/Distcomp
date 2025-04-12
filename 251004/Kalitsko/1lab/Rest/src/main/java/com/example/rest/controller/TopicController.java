package com.example.rest.controller;

import com.example.rest.dto.TopicRequestTo;
import com.example.rest.dto.TopicResponseTo;
import com.example.rest.dto.TopicUpdate;
import com.example.rest.service.CreatorNotFoundException;
import com.example.rest.service.TopicService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1.0/topics")
public class TopicController {

    private final TopicService topicService;

    @Autowired
    public TopicController(TopicService topicService) {
        this.topicService = topicService;
    }

    @GetMapping
    public ResponseEntity<List<TopicResponseTo>> findAll() {
        return ResponseEntity.ok(topicService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<TopicResponseTo> findById(@PathVariable Long id) {
        Optional<TopicResponseTo> creator = topicService.findById(id);
        return creator.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<TopicResponseTo> create(@Valid @RequestBody TopicRequestTo creatorRequestTo) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(topicService.create(creatorRequestTo));
    }

    @PutMapping()
    public ResponseEntity<TopicResponseTo> update(@Valid @RequestBody TopicUpdate creatorUpdate) {
        return ResponseEntity.ok(topicService.update(creatorUpdate));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        topicService.findById(id).orElseThrow(() -> new CreatorNotFoundException(id));
        topicService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
