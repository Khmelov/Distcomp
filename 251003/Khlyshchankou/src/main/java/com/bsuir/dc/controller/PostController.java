package com.bsuir.dc.controller;

import com.bsuir.dc.dto.request.PostRequestTo;
import com.bsuir.dc.dto.response.PostResponseTo;
import com.bsuir.dc.service.PostService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1.0/posts")
public class PostController {
    private final PostService postService;

    @Autowired
    public PostController(PostService postService) { this.postService = postService; }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public PostResponseTo createPost(@RequestBody @Valid PostRequestTo postRequestTo) { return postService.save(postRequestTo); }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<PostResponseTo> getAllPosts() { return postService.findAll(); }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public PostResponseTo getPostById(@PathVariable Long id) { return postService.findById(id); }

    @PutMapping
    @ResponseStatus(HttpStatus.OK)
    public PostResponseTo updatePost(@RequestBody @Valid PostRequestTo postRequestTo){ return postService.update(postRequestTo); }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deletePost(@PathVariable long id){ postService.deleteById(id); }
}
