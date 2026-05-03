package org.example.newsapi.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.newsapi.dto.request.CommentRequestTo;
import org.example.newsapi.dto.response.CommentResponseTo;
import org.example.newsapi.service.CommentService;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1.0/comments")
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CommentResponseTo create(@RequestBody @Valid CommentRequestTo request) {
        return commentService.create(request);
    }

    @GetMapping
    public List<CommentResponseTo> getAll(@PageableDefault(size = 50) Pageable pageable) {
        // В discussion сервисе пагинация не реализована, поэтому игнорируем pageable
        return commentService.findAll();
    }

    @GetMapping("/{id}")
    public CommentResponseTo getById(@PathVariable Long id) {
        return commentService.findById(id);
    }

    @PutMapping("/{id}")
    public CommentResponseTo update(@PathVariable Long id, @RequestBody @Valid CommentRequestTo request) {
        return commentService.update(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        commentService.delete(id);
    }
}