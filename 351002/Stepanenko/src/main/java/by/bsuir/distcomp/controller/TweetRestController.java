package by.bsuir.distcomp.controller;

import by.bsuir.distcomp.dto.request.TweetRequestTo;
import by.bsuir.distcomp.dto.response.AuthorResponseTo;
import by.bsuir.distcomp.dto.response.TweetResponseTo;
import by.bsuir.distcomp.core.service.AuthorService;
import by.bsuir.distcomp.core.service.TweetService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/api/v1.0/tweets")
public class TweetRestController {
    private final TweetService tweetService;
    private final AuthorService authorService;

    public TweetRestController(TweetService tweetService, AuthorService authorService) {
        this.tweetService = tweetService;
        this.authorService = authorService;
    }

    @PostMapping
    public ResponseEntity<TweetResponseTo> create(@Valid @RequestBody TweetRequestTo createRequest) {
        TweetResponseTo createdTweet = tweetService.create(createRequest);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(createdTweet);
    }

    @GetMapping("/{id}")
    public ResponseEntity<TweetResponseTo> getById(@PathVariable("id") Long tweetId) {
        TweetResponseTo tweet = tweetService.getById(tweetId);
        return ResponseEntity
                .ok(tweet);
    }

    @GetMapping
    public ResponseEntity<List<TweetResponseTo>> getAll() {
        List<TweetResponseTo> tweets = tweetService.getAll();
        return ResponseEntity
                .ok(tweets);
    }

    @PutMapping
    public ResponseEntity<TweetResponseTo> update(@Valid @RequestBody TweetRequestTo updateRequest) {
        TweetResponseTo updatedTweet = tweetService.update(updateRequest);
        return ResponseEntity
                .ok(updatedTweet);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteById(@PathVariable("id") Long tweetId) {
        tweetService.deleteById(tweetId);
        return ResponseEntity
                .noContent()
                .build();
    }

    @GetMapping("/{id}/author")
    public ResponseEntity<AuthorResponseTo> getAuthorByTweetId(@PathVariable("id") Long tweetId) {
        TweetResponseTo tweet = tweetService.getById(tweetId);
        AuthorResponseTo author = authorService.getById(tweet.getAuthorId());
        return ResponseEntity
                .ok(author);
    }

    @GetMapping("/search")
    public ResponseEntity<List<TweetResponseTo>> searchTweets(
            @RequestParam(required = false) Set<String> markerNames,
            @RequestParam(required = false) Set<Long> markerIds,
            @RequestParam(required = false) String authorLogin,
            @RequestParam(required = false) String title,
            @RequestParam(required = false) String content) {
        List<TweetResponseTo> filteredTweets = tweetService.getTweetsByMarkerNames(
                markerNames, markerIds, authorLogin, title, content);
        return ResponseEntity
                .ok(filteredTweets);
    }
}
