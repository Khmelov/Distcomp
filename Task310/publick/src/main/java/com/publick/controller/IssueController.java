package com.publick.controller;

import com.publick.dto.IssueRequestTo;
import com.publick.dto.IssueResponseTo;
import com.publick.service.IssueService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/issues")
@Validated
public class IssueController {

    @Autowired
    private IssueService issueService;

    @GetMapping
    public ResponseEntity<List<IssueResponseTo>> getAllIssues() {
        List<IssueResponseTo> issues = issueService.getAll();
        return ResponseEntity.ok(issues);
    }

    @GetMapping("/{id}")
    public ResponseEntity<IssueResponseTo> getIssueById(@PathVariable Long id) {
        IssueResponseTo issue = issueService.getById(id);
        return ResponseEntity.ok(issue);
    }

    @PostMapping
    public ResponseEntity<IssueResponseTo> createIssue(@Valid @RequestBody IssueRequestTo request) {
        IssueResponseTo created = issueService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PutMapping("/{id}")
    public ResponseEntity<IssueResponseTo> updateIssue(@PathVariable Long id, @Valid @RequestBody IssueRequestTo request) {
        IssueResponseTo updated = issueService.update(id, request);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteIssue(@PathVariable Long id) {
        issueService.delete(id);
        return ResponseEntity.noContent().build();
    }
}