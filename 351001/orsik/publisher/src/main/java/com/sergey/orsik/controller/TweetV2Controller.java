package com.sergey.orsik.controller;

import com.sergey.orsik.dto.request.TweetRequestTo;
import com.sergey.orsik.dto.response.TweetResponseTo;
import com.sergey.orsik.service.SecuredResourceService;
import com.sergey.orsik.service.TweetService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v2.0/tweets")
public class TweetV2Controller {

    private final TweetService tweetService;
    private final SecuredResourceService securedResourceService;

    public TweetV2Controller(TweetService tweetService, SecuredResourceService securedResourceService) {
        this.tweetService = tweetService;
        this.securedResourceService = securedResourceService;
    }

    @GetMapping
    public List<TweetResponseTo> findAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir,
            @RequestParam(required = false) Long creatorId,
            @RequestParam(required = false) String title) {
        return tweetService.findAll(page, size, sortBy, sortDir, creatorId, title);
    }

    @GetMapping("/{id}")
    public TweetResponseTo findById(@PathVariable Long id) {
        return tweetService.findById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public TweetResponseTo create(@Valid @RequestBody TweetRequestTo request) {
        return securedResourceService.createTweet(request);
    }

    @PutMapping
    public TweetResponseTo updateByBody(@Valid @RequestBody TweetRequestTo request) {
        if (request.getId() == null) {
            throw new IllegalArgumentException("id is required for update");
        }
        return securedResourceService.updateTweet(request.getId(), request);
    }

    @PutMapping("/{id}")
    public TweetResponseTo update(@PathVariable Long id, @Valid @RequestBody TweetRequestTo request) {
        return securedResourceService.updateTweet(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteById(@PathVariable Long id) {
        securedResourceService.deleteTweet(id);
    }
}
