package com.publick.controller;

import com.publick.dto.PostRequestTo;
import com.publick.dto.PostResponseTo;
import com.publick.service.PostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/v1.0/posts")
@Validated
public class PostController {

    @Autowired
    private PostService postService;

    @GetMapping
    public ResponseEntity<List<PostResponseTo>> getAllPosts() {
        List<PostResponseTo> posts = postService.getAll();
        return ResponseEntity.ok(posts);
    }

    @GetMapping("/paged")
    public ResponseEntity<Page<PostResponseTo>> getAllPostsPaged(@PageableDefault(size = 10) Pageable pageable) {
        Page<PostResponseTo> posts = postService.getAll(pageable);
        return ResponseEntity.ok(posts);
    }

    @GetMapping("/{id}")
    public ResponseEntity<PostResponseTo> getPostById(@PathVariable String id) {
        try {
            Long postId = Long.parseLong(id);
            PostResponseTo post = postService.getById(postId);
            return ResponseEntity.ok(post);
        } catch (NumberFormatException e) {
            return ResponseEntity.ok(null);
        }
    }

    @PostMapping
    public ResponseEntity<PostResponseTo> createPost(@Valid @RequestBody PostRequestTo request) {
        PostResponseTo created = postService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PutMapping("/{id}")
    public ResponseEntity<PostResponseTo> updatePost(@PathVariable String id, @Valid @RequestBody PostRequestTo request) {
        try {
            Long postId = Long.parseLong(id);
            PostResponseTo updated = postService.update(postId, request);
            return ResponseEntity.ok(updated);
        } catch (NumberFormatException e) {
            return ResponseEntity.ok(null);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePost(@PathVariable String id) {
        try {
            Long postId = Long.parseLong(id);
            postService.delete(postId);
            return ResponseEntity.noContent().build();
        } catch (NumberFormatException e) {
            return ResponseEntity.noContent().build();
        }
    }
}