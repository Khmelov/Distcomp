package com.example.demo.controllers.v1;

import com.example.demo.dto.requests.IssueRequestTo;
import com.example.demo.dto.responses.IssueResponseTo;
import com.example.demo.service.IssueService;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1.0/issues")
@Validated
public class IssueController {
    private final IssueService issueService;

    public IssueController(IssueService issueService) {
        this.issueService = issueService;
    }

    @PostMapping
    public ResponseEntity<IssueResponseTo> create(@Valid @RequestBody IssueRequestTo dto) {
        IssueResponseTo response = issueService.create(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    public ResponseEntity<?> findAll(
            @RequestParam(name = "page", required = false) Integer page,
            @RequestParam(name = "size", required = false) Integer size,
            @RequestParam(name = "sort",defaultValue = "id,asc") String sort,
            @RequestParam(name = "title",required = false) String title,
            @RequestParam(name = "content",required = false) String content,
            @RequestParam(name = "authorId",required = false) Long authorId,
            @RequestParam(name = "markName", required = false) String markName) {

        if (page != null && size != null) {
            Pageable pageable = PageRequest.of(page, size, parseSort(sort));
            return ResponseEntity.ok(issueService.findAll(pageable, title, content, authorId, markName));
        } else {
            return ResponseEntity.ok(issueService.findAll(title, content, authorId, markName));
        }
    }

    private Sort parseSort(String sort) {
        String[] parts = sort.split(",");
        String field = parts[0];
        Sort.Direction direction = parts.length > 1 && "desc".equalsIgnoreCase(parts[1])
                ? Sort.Direction.DESC : Sort.Direction.ASC;
        return Sort.by(direction, field);
    }

    @GetMapping("/{id}")
    public ResponseEntity<IssueResponseTo> findById(@PathVariable("id") Long id) {
        return ResponseEntity.ok(issueService.findById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<IssueResponseTo> update(@PathVariable("id") Long id,
                                                  @Valid @RequestBody IssueRequestTo dto) {
        IssueResponseTo updated = issueService.update(id, dto);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable("id") Long id) {
        issueService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
