package by.yelkin.TopicService.controller;

import by.yelkin.TopicService.dto.topic.TopicRq;
import by.yelkin.TopicService.dto.topic.TopicRs;
import by.yelkin.TopicService.dto.topic.TopicUpdateRq;
import by.yelkin.TopicService.service.TopicService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1.0/topics")
public class TopicController {
    private final TopicService topicService;

    @PostMapping
    public ResponseEntity<TopicRs> create(@Valid @RequestBody TopicRq rq) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(topicService.create(rq));
    }

    @GetMapping("/{id}")
    public ResponseEntity<TopicRs> readById(@Valid @NotNull @PathVariable("id") Long id) {
        return ResponseEntity.ok().body(topicService.readById(id));
    }

    @GetMapping
    public ResponseEntity<List<TopicRs>> readAll() {
        return ResponseEntity.ok().body(topicService.readAll());
    }

    @PutMapping
    public ResponseEntity<TopicRs> update(@Valid @RequestBody TopicUpdateRq rq) {
        return ResponseEntity.ok().body(topicService.update(rq));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteById(@Valid @NotNull @PathVariable("id") Long id) {
        topicService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}