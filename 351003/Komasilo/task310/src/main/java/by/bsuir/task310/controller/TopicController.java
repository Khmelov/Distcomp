package by.bsuir.task310.controller;

import by.bsuir.task310.dto.TopicRequestTo;
import by.bsuir.task310.dto.TopicResponseTo;
import by.bsuir.task310.service.TopicService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1.0/topics")
public class TopicController {

    private final TopicService service;

    public TopicController(TopicService service) {
        this.service = service;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public TopicResponseTo create(@RequestBody TopicRequestTo requestTo) {
        return service.create(requestTo);
    }

    @GetMapping
    public List<TopicResponseTo> getAll() {
        return service.getAll();
    }

    @GetMapping("/{id}")
    public TopicResponseTo getById(@PathVariable Long id) {
        return service.getById(id);
    }

    @PutMapping
    public TopicResponseTo update(@RequestBody TopicRequestTo requestTo) {
        return service.update(requestTo);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }
}