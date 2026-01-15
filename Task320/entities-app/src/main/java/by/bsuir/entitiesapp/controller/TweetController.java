package by.bsuir.entitiesapp.controller;

import by.bsuir.entitiesapp.dto.TweetRequestTo;
import by.bsuir.entitiesapp.dto.TweetResponseTo;
import by.bsuir.entitiesapp.service.TweetService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1.0/tweets")
public class TweetController {

    private final TweetService service;

    public TweetController(TweetService service) {
        this.service = service;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public TweetResponseTo create(@RequestBody TweetRequestTo dto) {
        return service.create(dto);
    }

    @GetMapping
    public List<TweetResponseTo> getAll() {
        return service.getAll();
    }

    @GetMapping("/{id}")
    public TweetResponseTo get(@PathVariable Long id) {
        return service.get(id);
    }

    @PutMapping("/{id}")
    public TweetResponseTo update(@PathVariable Long id, @RequestBody TweetRequestTo dto) {
        return service.update(id, dto);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }
}
