package com.example.task310.controller;

import com.example.task310.dto.*;
import com.example.task310.service.IssueService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping({"/api/v1.0/issues", "/api/v2.0/issues"})
@RequiredArgsConstructor
public class IssueController {
    private final IssueService service;

    @GetMapping
    public List<IssueResponseTo> getAll(Pageable pageable) {
        return service.getAll(pageable);
    }

    @GetMapping("/{id}")
    public IssueResponseTo getById(@PathVariable Long id) {
        return service.getById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("isAnonymous() or @accessGuard.canManageIssueByWriter(#dto.writerId, authentication)")
    public IssueResponseTo create(@Valid @RequestBody IssueRequestTo dto) {
        return service.create(dto);
    }

    @PutMapping("/{id}")
    @PreAuthorize("isAnonymous() or @accessGuard.canManageIssue(#id, authentication)")
    public IssueResponseTo update(@PathVariable Long id, @Valid @RequestBody IssueRequestTo dto) {
        dto.setId(id);
        return service.update(dto);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("isAnonymous() or @accessGuard.canManageIssue(#id, authentication)")
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }
}
