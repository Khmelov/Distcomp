package com.example.task310.controller;

import com.example.task310.dto.IssueRequestTo;
import com.example.task310.dto.IssueResponseTo;
import com.example.task310.service.IssueService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1.0/issues")
@RequiredArgsConstructor
public class IssueController {
    private final IssueService service;

    @GetMapping
    public List<IssueResponseTo> getAll() { return service.getAll(); }

    @GetMapping("/{id}")
    public IssueResponseTo getById(@PathVariable Long id) { return service.getById(id); }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public IssueResponseTo create(@Valid @RequestBody IssueRequestTo dto) {
        return service.create(dto);
    }

    @PutMapping
    public IssueResponseTo update(@Valid @RequestBody IssueRequestTo dto) {
        return service.update(dto);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) { service.delete(id); }
}