package com.sergey.orsik.controller;

import com.sergey.orsik.dto.request.CreatorRequestTo;
import com.sergey.orsik.dto.response.CreatorResponseTo;
import com.sergey.orsik.service.CreatorService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1.0/creators")
public class CreatorController {

    private final CreatorService creatorService;

    public CreatorController(CreatorService creatorService) {
        this.creatorService = creatorService;
    }

    @GetMapping
    public List<CreatorResponseTo> findAll() {
        return creatorService.findAll();
    }

    @GetMapping("/{id}")
    public CreatorResponseTo findById(@PathVariable Long id) {
        return creatorService.findById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CreatorResponseTo create(@Valid @RequestBody CreatorRequestTo request) {
        return creatorService.create(request);
    }

    @PutMapping
    public CreatorResponseTo updateByBody(@Valid @RequestBody CreatorRequestTo request) {
        if (request.getId() == null) {
            throw new IllegalArgumentException("id is required for update");
        }
        return creatorService.update(request.getId(), request);
    }

    @PutMapping("/{id}")
    public CreatorResponseTo update(@PathVariable Long id, @Valid @RequestBody CreatorRequestTo request) {
        return creatorService.update(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteById(@PathVariable Long id) {
        creatorService.deleteById(id);
    }
}
