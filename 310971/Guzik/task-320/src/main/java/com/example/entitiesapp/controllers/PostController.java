package com.example.entitiesapp.controllers;

import com.example.entitiesapp.dto.request.PostRequestTo;
import com.example.entitiesapp.dto.response.PostResponseTo;
import com.example.entitiesapp.services.PostService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1.0/posts")
@RequiredArgsConstructor
public class PostController {
    private final PostService postService;

    @GetMapping
    public ResponseEntity<List<PostResponseTo>> getAllPosts() {
        List<PostResponseTo> posts = postService.getAll();
        return ResponseEntity.ok(posts);
    }

    @GetMapping("/{id}")
    public ResponseEntity<PostResponseTo> getPostById(@PathVariable Long id) {
        PostResponseTo post = postService.getById(id);
        return ResponseEntity.ok(post);
    }

    @PostMapping
    public ResponseEntity<PostResponseTo> createPost(@Valid @RequestBody PostRequestTo postRequestTo) {
        PostResponseTo createdPost = postService.create(postRequestTo);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdPost);
    }

    @PutMapping("/{id}")
    public ResponseEntity<PostResponseTo> updatePost(
            @PathVariable Long id,
            @Valid @RequestBody PostRequestTo postRequestTo) {
        PostResponseTo updatedPost = postService.update(id, postRequestTo);
        return ResponseEntity.ok(updatedPost);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePost(@PathVariable Long id) {
        postService.delete(id);
        return ResponseEntity.noContent().build();
    }
}