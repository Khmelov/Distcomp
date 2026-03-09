package com.example.restApi.controllers;

import com.example.restApi.dto.request.IssueRequestTo;
import com.example.restApi.dto.response.IssueResponseTo;
import com.example.restApi.services.IssueService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1.0/issues")
public class IssueController {

    private final IssueService issueService;

    public IssueController(IssueService issueService) {
        this.issueService = issueService;
    }

    @GetMapping
    public ResponseEntity<List<IssueResponseTo>> getAll() {
        return ResponseEntity.ok(issueService.getAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<IssueResponseTo> getById(@PathVariable Long id) {
        return ResponseEntity.ok(issueService.getById(id));
    }

    @PostMapping
    public ResponseEntity<IssueResponseTo> create(@Valid @RequestBody IssueRequestTo request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(issueService.create(request));
    }

    @PutMapping
    public ResponseEntity<IssueResponseTo> update(@Valid @RequestBody IssueRequestTo request) {
        return ResponseEntity.ok(issueService.update(request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        issueService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
