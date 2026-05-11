package org.polozkov.controller.secured.issue;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.polozkov.dto.issue.IssueRequestTo;
import org.polozkov.dto.issue.IssueResponseTo;
import org.polozkov.service.issue.IssueService;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v2.0/issues")
@RequiredArgsConstructor
public class IssueControllerSecured {

    private final IssueService issueService;

    @GetMapping
    public List<IssueResponseTo> getAllIssues() {
        return issueService.getAllIssues();
    }

    @GetMapping("/{id}")
    public IssueResponseTo getIssue(@PathVariable Long id) {
        return issueService.getIssue(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public IssueResponseTo createIssue(@Valid @RequestBody IssueRequestTo issueRequest) {
        return issueService.createIssue(issueRequest);
    }

    @PutMapping
    @PreAuthorize("hasRole('ADMIN') or @issueSecurity.isOwner(#issueRequest.id)")
    public IssueResponseTo updateIssue(@Valid @RequestBody IssueRequestTo issueRequest) {
        return issueService.updateIssue(issueRequest);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasRole('ADMIN') or @issueSecurity.isOwner(#id)")
    public void deleteIssue(@PathVariable Long id) {
        issueService.deleteIssue(id);
    }
}