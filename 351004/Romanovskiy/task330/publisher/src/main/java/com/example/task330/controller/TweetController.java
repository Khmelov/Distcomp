package com.example.task330.controller;

import com.example.task330.domain.dto.request.TweetRequestTo;
import com.example.task330.domain.dto.response.TweetResponseTo;
import com.example.task330.service.TweetService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/tweets")
@RequiredArgsConstructor
public class TweetController {

    private final TweetService tweetService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public TweetResponseTo create(@Valid @RequestBody TweetRequestTo request) {
        return tweetService.create(request);
    }

    @GetMapping
    public List<TweetResponseTo> findAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        return tweetService.findAll(page, size);
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
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteById(@PathVariable Long id) {
        tweetService.deleteById(id);
    }
}