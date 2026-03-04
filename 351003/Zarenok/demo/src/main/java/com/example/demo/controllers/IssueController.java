package com.example.demo.controllers;

import com.example.demo.dto.requests.IssueRequestTo;
import com.example.demo.dto.responses.AuthorResponseTo;
import com.example.demo.dto.responses.IssueResponseTo;
import com.example.demo.service.IssueService;
import org.springframework.data.crossstore.ChangeSetPersister;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;

import java.util.List;

@RestController
@RequestMapping("/api/v1.0/issues")
@Validated
public class IssueController {
    private final IssueService issueService;

    public IssueController(IssueService issueService) {
        this.issueService = issueService;
    }

    @PostMapping
    public ResponseEntity<IssueResponseTo> create(@Valid @RequestBody IssueRequestTo dto) throws ChangeSetPersister.NotFoundException {
        IssueResponseTo response = issueService.create(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    public ResponseEntity<List<IssueResponseTo>> findAll() {
        return ResponseEntity.ok(issueService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<IssueResponseTo> findById(@PathVariable Long id) throws ChangeSetPersister.NotFoundException {
        IssueResponseTo issue = issueService.findById(id);
        return ResponseEntity.status(HttpStatus.CREATED).body(issue);
    }

    @PutMapping("/{id}")
    public ResponseEntity<IssueResponseTo> update(@Valid @PathVariable Long id,
                                                   @RequestBody IssueRequestTo dto) throws ChangeSetPersister.NotFoundException {
        IssueResponseTo updated = issueService.update(id, dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) throws ChangeSetPersister.NotFoundException {
        issueService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
