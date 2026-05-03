package com.example.demo.controllers.v2;

import com.example.demo.dto.requests.IssueRequestTo;
import com.example.demo.dto.responses.IssueResponseTo;
import com.example.demo.service.IssueService;
import jakarta.validation.Valid;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v2.0/issues")
public class IssueControllerV2 {

    private final IssueService issueService;

    public IssueControllerV2(IssueService issueService) {
        this.issueService = issueService;
    }

    @PostMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<IssueResponseTo> create(@Valid @RequestBody IssueRequestTo dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(issueService.create(dto));
    }

    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> findAll(
            @RequestParam(name = "page", required = false) Integer page,
            @RequestParam(name = "size", required = false) Integer size,
            @RequestParam(name = "sort", defaultValue = "id,asc") String sort,
            @RequestParam(name = "title", required = false) String title,
            @RequestParam(name = "content", required = false) String content,
            @RequestParam(name = "authorId", required = false) Long authorId,
            @RequestParam(name = "markName", required = false) String markName) {
        if (page != null && size != null) {
            Pageable pageable = PageRequest.of(page, size, parseSort(sort));
            return ResponseEntity.ok(issueService.findAll(pageable, title, content, authorId, markName));
        } else {
            return ResponseEntity.ok(issueService.findAll(title, content, authorId, markName));
        }
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or (hasRole('CUSTOMER') and @issueService.isOwnerOfIssue(#id, authentication.name))")
    public ResponseEntity<IssueResponseTo> findById(@PathVariable("id") Long id) {
        return ResponseEntity.ok(issueService.findById(id));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or (hasRole('CUSTOMER') and @issueService.isOwnerOfIssue(#id, authentication.name))")
    public ResponseEntity<IssueResponseTo> update(@PathVariable("id") Long id, @Valid @RequestBody IssueRequestTo dto) {
        return ResponseEntity.ok(issueService.update(id, dto));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> delete(@PathVariable("id") Long id) {
        issueService.delete(id);
        return ResponseEntity.noContent().build();
    }

    private Sort parseSort(String sort) {
        String[] parts = sort.split(",");
        String field = parts[0];
        Sort.Direction direction = parts.length > 1 && "desc".equalsIgnoreCase(parts[1])
                ? Sort.Direction.DESC : Sort.Direction.ASC;
        return Sort.by(direction, field);
    }
}
