package com.task310.blogplatform.controller.v2;

import com.task310.blogplatform.dto.PostRequestTo;
import com.task310.blogplatform.dto.PostResponseTo;
import com.task310.blogplatform.service.CurrentUserService;
import com.task310.blogplatform.service.PostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v2.0")
public class PostControllerV2 {
    private final PostService postService;
    private final CurrentUserService currentUserService;

    @Autowired
    public PostControllerV2(PostService postService, CurrentUserService currentUserService) {
        this.postService = postService;
        this.currentUserService = currentUserService;
    }

    @PostMapping("/posts")
    @PreAuthorize("hasAnyRole('ADMIN', 'CUSTOMER')")
    public ResponseEntity<PostResponseTo> createPost(@RequestBody PostRequestTo postRequestTo) {
        PostResponseTo created = postService.create(postRequestTo);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @GetMapping("/posts")
    @PreAuthorize("hasAnyRole('ADMIN', 'CUSTOMER')")
    public ResponseEntity<List<PostResponseTo>> getAllPosts() {
        List<PostResponseTo> posts = postService.findAll();
        return ResponseEntity.ok(posts);
    }

    @GetMapping("/posts/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'CUSTOMER')")
    public ResponseEntity<PostResponseTo> getPostById(@PathVariable Long id) {
        PostResponseTo post = postService.findById(id);
        return ResponseEntity.ok(post);
    }

    @PutMapping("/posts/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<PostResponseTo> updatePost(@PathVariable Long id, @RequestBody PostRequestTo postRequestTo) {
        PostResponseTo updated = postService.update(id, postRequestTo);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/posts/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deletePost(@PathVariable Long id) {
        postService.delete(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @GetMapping("/articles/{id}/posts")
    @PreAuthorize("hasAnyRole('ADMIN', 'CUSTOMER')")
    public ResponseEntity<List<PostResponseTo>> getPostsByArticleId(@PathVariable Long id) {
        List<PostResponseTo> posts = postService.getPostsByArticleId(id);
        return ResponseEntity.ok(posts);
    }
}

