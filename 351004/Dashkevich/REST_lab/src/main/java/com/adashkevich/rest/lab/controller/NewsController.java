package com.adashkevich.rest.lab.controller;

import com.adashkevich.rest.lab.dto.request.NewsRequestTo;
import com.adashkevich.rest.lab.dto.response.EditorResponseTo;
import com.adashkevich.rest.lab.dto.response.MarkerResponseTo;
import com.adashkevich.rest.lab.dto.response.MessageResponseTo;
import com.adashkevich.rest.lab.dto.response.NewsResponseTo;
import com.adashkevich.rest.lab.service.NewsService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1.0/news")
@Validated
public class NewsController {
    private final NewsService service;

    public NewsController(NewsService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<NewsResponseTo> create(@Valid @RequestBody NewsRequestTo body) {
        return ResponseEntity.status(201).body(service.create(body));
    }

    @GetMapping
    public List<NewsResponseTo> getAll() {
        return service.getAll();
    }

    @GetMapping("/{id}")
    public NewsResponseTo getById(@PathVariable @Positive Long id) {
        return service.getById(id);
    }

    @PutMapping("/{id}")
    public NewsResponseTo update(@PathVariable @Positive Long id, @Valid @RequestBody NewsRequestTo body) {
        return service.update(id, body);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable @Positive Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{newsId}/editor")
    public EditorResponseTo getEditorByNewsId(@PathVariable @Positive Long newsId) {
        return service.getEditorByNewsId(newsId);
    }

    @GetMapping("/{newsId}/markers")
    public List<MarkerResponseTo> getMarkersByNewsId(@PathVariable @Positive Long newsId) {
        return service.getMarkersByNewsId(newsId);
    }

    @GetMapping("/{newsId}/messages")
    public List<MessageResponseTo> getMessagesByNewsId(@PathVariable @Positive Long newsId) {
        return service.getMessagesByNewsId(newsId);
    }
}
