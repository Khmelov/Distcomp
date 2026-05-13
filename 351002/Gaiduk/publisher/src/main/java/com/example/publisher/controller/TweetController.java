package com.example.publisher.controller;

import com.example.publisher.dto.TweetRequestTo;
import com.example.publisher.dto.TweetResponseTo;
import com.example.publisher.service.TweetService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1.0/tweets")
@RequiredArgsConstructor
public class TweetController {

    private final TweetService service;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public TweetResponseTo create(@Valid @RequestBody TweetRequestTo dto) {
        return service.create(dto);
    }

    @GetMapping
    public List<TweetResponseTo> getAll(Pageable pageable) {
        return service.getAll(pageable).getContent();
    }

    @GetMapping("/{id}")
    public TweetResponseTo get(@PathVariable("id") Long id) {
        return service.get(id);
    }

    @PutMapping("/{id}")
    public TweetResponseTo update(@PathVariable("id") Long id, @Valid @RequestBody TweetRequestTo dto) {
        return service.update(id, dto);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable("id") Long id) {
        service.delete(id);
    }
}