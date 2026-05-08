package com.example.demo.controller;

import com.example.demo.dto.CommentRequest;
import com.example.demo.entity.Comment;
import com.example.demo.service.CommentService;
import com.example.demo.specification.CommentSpecification;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.context.annotation.Profile;
import java.util.List;

@RestController
@RequestMapping("/api/v1.0/comments")
@Profile("docker")
public class CommentController {
    private final CommentService commentService;

    public CommentController(CommentService commentService) {
        this.commentService = commentService;
    }

    @PostMapping
    public ResponseEntity<Comment> create(@Valid @RequestBody CommentRequest request) {
        Comment comment = commentService.createFromRequest(request);
        return new ResponseEntity<>(comment, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Comment> update(@PathVariable Long id, @Valid @RequestBody CommentRequest request) {
        Comment comment = commentService.updateFromRequest(id, request);
        return ResponseEntity.ok(comment);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        commentService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Comment> findById(@PathVariable Long id) {
        return commentService.findById(id).map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    // Плоский список (без пагинации) – для тестов
    @GetMapping
    public ResponseEntity<List<Comment>> findAllList(
            @RequestParam(required = false) String content,
            @RequestParam(required = false) Long newsId,
            @RequestParam(defaultValue = "id,asc") String[] sort) {

        Sort.Direction direction = Sort.Direction.fromString(sort[1]);
        Sort sorting = Sort.by(direction, sort[0]);

        Specification<Comment> spec = Specification.where(CommentSpecification.contentContains(content))
                .and(CommentSpecification.newsIdEquals(newsId));

        List<Comment> comments = commentService.findAll(spec, sorting);
        return ResponseEntity.ok(comments);
    }

    // Пагинация (по требованию задания)
    @GetMapping("/page")
    public ResponseEntity<Page<Comment>> findAllPage(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id,asc") String[] sort,
            @RequestParam(required = false) String content,
            @RequestParam(required = false) Long newsId) {

        Sort.Direction direction = Sort.Direction.fromString(sort[1]);
        Sort sorting = Sort.by(direction, sort[0]);
        Pageable pageable = PageRequest.of(page, size, sorting);

        Specification<Comment> spec = Specification.where(CommentSpecification.contentContains(content))
                .and(CommentSpecification.newsIdEquals(newsId));

        return ResponseEntity.ok(commentService.findAll(spec, pageable));
    }
}