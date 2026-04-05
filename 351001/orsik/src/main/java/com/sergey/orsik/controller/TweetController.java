package com.sergey.orsik.controller;

import com.sergey.orsik.dto.request.TweetRequestTo;
import com.sergey.orsik.dto.response.TweetResponseTo;
import com.sergey.orsik.service.TweetService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1.0/tweets")
public class TweetController {

    private static final Logger log = LoggerFactory.getLogger(TweetController.class);

    private final TweetService tweetService;

    public TweetController(TweetService tweetService) {
        this.tweetService = tweetService;
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
        log.info("CREATE /tweets request deserialized: creatorId={}, title='{}', content='{}'",
                request.getCreatorId(), request.getTitle(), request.getContent());
        return tweetService.create(request);
    }

    @PutMapping
    public TweetResponseTo updateByBody(@Valid @RequestBody TweetRequestTo request) {
        if (request.getId() == null) {
            throw new IllegalArgumentException("id is required for update");
        }
        return tweetService.update(request.getId(), request);
    }

    @PutMapping("/{id}")
    public TweetResponseTo update(@PathVariable Long id, @Valid @RequestBody TweetRequestTo request) {
        return tweetService.update(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteById(@PathVariable Long id) {
        tweetService.deleteById(id);
    }
}
