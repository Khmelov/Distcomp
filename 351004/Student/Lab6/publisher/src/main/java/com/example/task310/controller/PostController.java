package com.example.task310.controller;

import com.example.task310.dto.*;
import com.example.task310.service.PostService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping({"/api/v1.0/posts", "/api/v2.0/posts"})
@RequiredArgsConstructor
public class PostController {
    private final PostService service;

    @GetMapping
    public List<PostResponseTo> getAll(Pageable pageable) {
        return service.getAll(pageable);
    }

    @GetMapping("/{id}")
    public PostResponseTo getById(@PathVariable Long id) {
        return service.getById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("isAnonymous() or @accessGuard.canManagePostByIssue(#dto.issueId, authentication)")
    public PostResponseTo create(@Valid @RequestBody PostRequestTo dto) {
        return service.create(dto);
    }

    @PutMapping("/{id}")
    @PreAuthorize("isAnonymous() or @accessGuard.canManagePostById(#id, authentication)")
    public PostResponseTo update(@PathVariable Long id, @Valid @RequestBody PostRequestTo dto) {
        dto.setId(id);
        return service.update(dto);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("isAnonymous() or @accessGuard.canManagePostById(#id, authentication)")
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }
}
