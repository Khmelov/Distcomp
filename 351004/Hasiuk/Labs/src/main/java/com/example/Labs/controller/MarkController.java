package com.example.Labs.controller;

import com.example.Labs.dto.request.MarkRequestTo;
import com.example.Labs.dto.response.MarkResponseTo;
import com.example.Labs.service.MarkService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/v1.0/marks")
@RequiredArgsConstructor
public class MarkController {
    private final MarkService service;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public MarkResponseTo create(@Valid @RequestBody MarkRequestTo request) {
        return service.create(request);
    }

    @GetMapping
    public List<MarkResponseTo> getAll(Pageable pageable) {
        return service.getAll(pageable).getContent();
    }

    @GetMapping("/{id}")
    public MarkResponseTo getById(@PathVariable Long id) {
        return service.getById(id);
    }

    @PutMapping("/{id}")
    public MarkResponseTo update(@PathVariable Long id, @Valid @RequestBody MarkRequestTo request) {
        return service.update(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }
}