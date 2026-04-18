package com.example.forum.controller.v1;

import com.example.forum.dto.request.TopicRequestTo;
import com.example.forum.dto.response.TopicResponseTo;
import com.example.forum.service.TopicService;
import jakarta.validation.Valid;
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
    public TopicResponseTo create(@Valid @RequestBody TopicRequestTo request) {
        return service.create(request);
    }

    @GetMapping("/{id}")
    public TopicResponseTo getById(@PathVariable Long id) {
        return service.getById(id);
    }

    @GetMapping
    public List<TopicResponseTo> getAll() {
        return service.getAll();
    }

    @PutMapping("/{id}")
    public TopicResponseTo update(@PathVariable Long id,
                                  @Valid @RequestBody TopicRequestTo request) {
        return service.update(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }
}
