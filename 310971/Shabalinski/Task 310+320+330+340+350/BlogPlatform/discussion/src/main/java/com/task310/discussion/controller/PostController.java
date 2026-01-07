package com.task310.discussion.controller;

import com.task310.discussion.dto.PostRequestTo;
import com.task310.discussion.dto.PostResponseTo;
import com.task310.discussion.service.PostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1.0")
public class PostController {
    private final PostService postService;

    @Autowired
    public PostController(PostService postService) {
        this.postService = postService;
    }

    @PostMapping("/posts")
    public ResponseEntity<PostResponseTo> createPost(@RequestBody PostRequestTo postRequestTo) {
        PostResponseTo created = postService.create(postRequestTo);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @GetMapping("/posts")
    public ResponseEntity<List<PostResponseTo>> getAllPosts() {
        List<PostResponseTo> posts = postService.findAll();
        return ResponseEntity.ok(posts);
    }

    @GetMapping("/posts/{id}")
    public ResponseEntity<PostResponseTo> getPostById(@PathVariable Long id) {
        PostResponseTo post = postService.findById(id);
        return ResponseEntity.ok(post);
    }

    @PutMapping("/posts/{id}")
    public ResponseEntity<PostResponseTo> updatePost(@PathVariable Long id, @RequestBody PostRequestTo postRequestTo) {
        PostResponseTo updated = postService.update(id, postRequestTo);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/posts/{id}")
    public ResponseEntity<Void> deletePost(@PathVariable Long id) {
        postService.delete(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @GetMapping("/articles/{id}/posts")
    public ResponseEntity<List<PostResponseTo>> getPostsByArticleId(@PathVariable Long id) {
        List<PostResponseTo> posts = postService.getPostsByArticleId(id);
        return ResponseEntity.ok(posts);
    }
}

