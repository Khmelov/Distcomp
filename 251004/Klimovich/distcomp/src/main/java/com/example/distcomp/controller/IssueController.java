package com.example.distcomp.controller;

import com.example.distcomp.dto.IssueRequestTo;
import com.example.distcomp.dto.IssueResponseTo;
import com.example.distcomp.mapper.IssueMapper;
import com.example.distcomp.service.IssueService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(path = "/api/v1.0/issues")
public class IssueController {

    private final IssueService issueService;

    @Autowired
    public IssueController(IssueService service) {
        this.issueService = service;
    }

    @PostMapping
    public ResponseEntity<IssueResponseTo> createIssue(@RequestBody IssueRequestTo request) {
        IssueResponseTo response = issueService.createIssue(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    public ResponseEntity<List<IssueResponseTo>> getAllIssues() {
        List<IssueResponseTo> response = issueService.getAllIssues();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<IssueResponseTo> getIssueById(@PathVariable Long id) {
        IssueResponseTo response = issueService.getIssueById(id);
        return ResponseEntity.ok(response);
    }

    @PutMapping
    public ResponseEntity<IssueResponseTo> updateIssue(@RequestBody IssueRequestTo request) {
        IssueResponseTo response = issueService.updateIssue(request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteIssue(@PathVariable Long id) {
        issueService.deleteIssue(id);
        return ResponseEntity.noContent().build();
    }
}
