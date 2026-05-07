package by.bsuir.distcomp.controller;

import by.bsuir.distcomp.dto.request.TweetRequestTo;
import by.bsuir.distcomp.dto.response.AuthorResponseTo;
import by.bsuir.distcomp.dto.response.TweetResponseTo;
import by.bsuir.distcomp.service.AuthorService;
import by.bsuir.distcomp.service.TweetService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/api/v1.0/tweets")
public class TweetController {
    private final TweetService tweetService;
    private final AuthorService authorService;

    public TweetController(TweetService tweetService, AuthorService authorService) {
        this.tweetService = tweetService;
        this.authorService = authorService;
    }

    @PostMapping
    public ResponseEntity<TweetResponseTo> create(@Valid @RequestBody TweetRequestTo dto) {
        TweetResponseTo response = tweetService.create(dto);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<TweetResponseTo> getById(@PathVariable Long id) {
        TweetResponseTo response = tweetService.getById(id);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity<List<TweetResponseTo>> getAll() {
        List<TweetResponseTo> response = tweetService.getAll();
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PutMapping
    public ResponseEntity<TweetResponseTo> update(@Valid @RequestBody TweetRequestTo dto) {
        TweetResponseTo response = tweetService.update(dto);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteById(@PathVariable Long id) {
        tweetService.deleteById(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @GetMapping("/{id}/author")
    public ResponseEntity<AuthorResponseTo> getAuthorByTweetId(@PathVariable Long id) {
        TweetResponseTo tweet = tweetService.getById(id);
        AuthorResponseTo author = authorService.getById(tweet.getAuthorId());
        return new ResponseEntity<>(author, HttpStatus.OK);
    }

    @GetMapping("/search")
    public ResponseEntity<List<TweetResponseTo>> searchTweets(
            @RequestParam(required = false) Set<String> markerNames,
            @RequestParam(required = false) Set<Long> markerIds,
            @RequestParam(required = false) String authorLogin,
            @RequestParam(required = false) String title,
            @RequestParam(required = false) String content) {
        List<TweetResponseTo> response = tweetService.getTweetsByMarkerNames(
                markerNames, markerIds, authorLogin, title, content);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
