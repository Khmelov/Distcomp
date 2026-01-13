package com.publick.controller;

import com.publick.dto.IssueRequestTo;
import com.publick.dto.IssueResponseTo;
import com.publick.service.IssueService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
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

    @Autowired
    private IssueService issueService;

    @GetMapping
    public ResponseEntity<List<IssueResponseTo>> getAllIssues() {
        List<IssueResponseTo> issues = issueService.getAll();
        return ResponseEntity.ok(issues);
    }

    @GetMapping("/paged")
    public ResponseEntity<Page<IssueResponseTo>> getAllIssuesPaged(@PageableDefault(size = 10) Pageable pageable) {
        Page<IssueResponseTo> issues = issueService.getAll(pageable);
        return ResponseEntity.ok(issues);
    }

    @GetMapping("/{id}")
    public ResponseEntity<IssueResponseTo> getIssueById(@PathVariable String id) {
        try {
            Long issueId = Long.parseLong(id);
            IssueResponseTo issue = issueService.getById(issueId);
            return ResponseEntity.ok(issue);
        } catch (NumberFormatException e) {
            return ResponseEntity.ok(null);
        }
    }

    @PostMapping
    public ResponseEntity<IssueResponseTo> createIssue(@Valid @RequestBody IssueRequestTo request) {
        IssueResponseTo created = issueService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PutMapping("/{id}")
    public ResponseEntity<IssueResponseTo> updateIssue(@PathVariable String id, @Valid @RequestBody IssueRequestTo request) {
        try {
            Long issueId = Long.parseLong(id);
            IssueResponseTo updated = issueService.update(issueId, request);
            return ResponseEntity.ok(updated);
        } catch (NumberFormatException e) {
            return ResponseEntity.ok(null);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteIssue(@PathVariable String id) {
        try {
            Long issueId = Long.parseLong(id);
            issueService.delete(issueId);
            return ResponseEntity.noContent().build();
        } catch (NumberFormatException e) {
            return ResponseEntity.noContent().build();
        }
    }
}