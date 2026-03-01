package com.example.demo.controllers;

import com.example.demo.dto.requests.IssueRequestTo;
import com.example.demo.dto.responses.IssueResponseTo;
import com.example.demo.service.IssueService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

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
    public ResponseEntity<IssueResponseTo> create(@RequestBody IssueRequestTo dto){
        IssueResponseTo response = issueService.create(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    public ResponseEntity<Page<IssueResponseTo>> findAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir
    ){
        Sort.Direction direction =
                sortDir.equalsIgnoreCase("desc")
                        ? Sort.Direction.DESC
                        : Sort.Direction.ASC;

        Pageable pageable = PageRequest.of(page, size, direction, sortBy);

        return ResponseEntity.ok(issueService.findAll(pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<IssueResponseTo> findById(@PathVariable Long id){
        IssueResponseTo issue = issueService.findById(id);
        return ResponseEntity.ok(issue);
    }

    @PutMapping("/{id}")
    public ResponseEntity<IssueResponseTo> update(@PathVariable Long id,
                                                   @RequestBody IssueRequestTo dto){
        IssueResponseTo updated = issueService.update(id, dto);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id){
        issueService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
