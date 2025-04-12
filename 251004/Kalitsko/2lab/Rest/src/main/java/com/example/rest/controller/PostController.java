package com.example.rest.controller;

import com.example.rest.dto.post.PostRequestTo;
import com.example.rest.dto.post.PostResponseTo;
import com.example.rest.dto.post.PostUpdate;
import com.example.rest.exceptionHandler.CreatorNotFoundException;
import com.example.rest.service.PostService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1.0/posts")
public class PostController {

    private final PostService postService;

    public PostController(PostService postService) {
        this.postService = postService;
    }

    @GetMapping
    public ResponseEntity<List<PostResponseTo>> findAll() {
        return ResponseEntity.ok(postService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<PostResponseTo> findById(@PathVariable Long id) {
        Optional<PostResponseTo> creator = postService.findById(id);
        return creator.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<PostResponseTo> create(@Valid @RequestBody PostRequestTo creatorRequestTo) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(postService.create(creatorRequestTo));

    }

    @PutMapping()
    public ResponseEntity<PostResponseTo> update(@Valid @RequestBody PostUpdate creatorUpdate) {
        return ResponseEntity.ok(postService.update(creatorUpdate));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        postService.findById(id).orElseThrow(() -> new CreatorNotFoundException(id));
        postService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
