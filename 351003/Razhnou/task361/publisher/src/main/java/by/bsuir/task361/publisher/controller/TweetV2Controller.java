package by.bsuir.task361.publisher.controller;

import by.bsuir.task361.publisher.dto.request.TweetRequestTo;
import by.bsuir.task361.publisher.dto.response.TweetResponseTo;
import by.bsuir.task361.publisher.service.SecuredTweetService;
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
@RequestMapping("/api/v2.0/tweets")
public class TweetV2Controller {
    private final SecuredTweetService securedTweetService;

    public TweetV2Controller(SecuredTweetService securedTweetService) {
        this.securedTweetService = securedTweetService;
    }

    @PostMapping
    public ResponseEntity<TweetResponseTo> create(@Valid @RequestBody TweetRequestTo request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(securedTweetService.create(request));
    }

    @GetMapping
    public List<TweetResponseTo> findAll() {
        return securedTweetService.findAll();
    }

    @GetMapping("/{id}")
    public TweetResponseTo findById(@PathVariable Long id) {
        return securedTweetService.findById(id);
    }

    @PutMapping
    public TweetResponseTo update(@Valid @RequestBody TweetRequestTo request) {
        return securedTweetService.update(request);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        securedTweetService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
