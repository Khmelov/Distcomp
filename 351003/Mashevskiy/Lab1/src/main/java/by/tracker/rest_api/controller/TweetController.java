package by.tracker.rest_api.controller;

import by.tracker.rest_api.dto.TweetRequestTo;
import by.tracker.rest_api.dto.TweetResponseTo;
import by.tracker.rest_api.service.TweetService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
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
    public TweetResponseTo create(@Valid @RequestBody TweetRequestTo request) {
        return service.create(request);
    }

    @GetMapping
    public List<TweetResponseTo> getAll() {
        return service.getAll();
    }

    @GetMapping("/{id}")
    public TweetResponseTo getById(@PathVariable Long id) {
        return service.getById(id);
    }

    @PutMapping
    public TweetResponseTo update(@Valid @RequestBody TweetRequestTo request) {
        return service.update(request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }
}