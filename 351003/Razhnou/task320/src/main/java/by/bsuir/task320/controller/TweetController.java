package by.bsuir.task320.controller;

import by.bsuir.task320.dto.request.TweetRequestTo;
import by.bsuir.task320.dto.response.TweetResponseTo;
import by.bsuir.task320.service.TweetService;
import jakarta.validation.Valid;
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

import java.util.List;

@RestController
@RequestMapping("/api/v1.0/tweets")
public class TweetController {
    private final TweetService tweetService;

    public TweetController(TweetService tweetService) {
        this.tweetService = tweetService;
    }

    @PostMapping
    public ResponseEntity<TweetResponseTo> create(@Valid @RequestBody TweetRequestTo request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(tweetService.create(request));
    }

    @GetMapping
    public List<TweetResponseTo> findAll() {
        return tweetService.findAll();
    }

    @GetMapping("/{id}")
    public TweetResponseTo findById(@PathVariable Long id) {
        return tweetService.findById(id);
    }

    @PutMapping
    public TweetResponseTo update(@Valid @RequestBody TweetRequestTo request) {
        return tweetService.update(request);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        tweetService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
