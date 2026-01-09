package org.example.task310rest.controller;

import jakarta.validation.Valid;
import java.util.List;
import org.example.task310rest.dto.LabelResponseTo;
import org.example.task310rest.dto.MessageResponseTo;
import org.example.task310rest.dto.TweetRequestTo;
import org.example.task310rest.dto.TweetResponseTo;
import org.example.task310rest.dto.WriterResponseTo;
import org.example.task310rest.service.TweetService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1.0/tweets")
public class TweetController {

    private final TweetService service;

    public TweetController(TweetService service) {
        this.service = service;
    }

    @GetMapping
    public List<TweetResponseTo> getAll() {
        return service.getAll();
    }

    @GetMapping("/{id}")
    public TweetResponseTo getById(@PathVariable("id") Long id) {
        return service.getById(id);
    }

    @PostMapping
    public ResponseEntity<TweetResponseTo> create(@Valid @RequestBody TweetRequestTo request) {
        TweetResponseTo response = service.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/{id}")
    public TweetResponseTo update(@PathVariable("id") Long id, @Valid @RequestBody TweetRequestTo request) {
        return service.update(id, request);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable("id") Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}/writer")
    public WriterResponseTo getWriter(@PathVariable("id") Long id) {
        return service.getWriterByTweetId(id);
    }

    @GetMapping("/{id}/labels")
    public List<LabelResponseTo> getLabels(@PathVariable("id") Long id) {
        return service.getLabelsByTweetId(id);
    }

    @GetMapping("/{id}/messages")
    public List<MessageResponseTo> getMessages(@PathVariable("id") Long id) {
        return service.getMessagesByTweetId(id);
    }
}


