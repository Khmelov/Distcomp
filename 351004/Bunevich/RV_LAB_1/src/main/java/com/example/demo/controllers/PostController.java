package com.example.demo.controllers;

import com.example.demo.dto.request.PostRequestTo;
import com.example.demo.dto.request.StoryRequestTo;
import com.example.demo.dto.response.PostResponseTo;
import com.example.demo.dto.response.StoryResponseTo;
import com.example.demo.servises.PostService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1.0/posts")
public class PostController {

    private final PostService postService;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<PostResponseTo> getPosts(){
        return postService.getPosts();
    }
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public PostResponseTo createStory(@Valid @RequestBody PostRequestTo postRequestTo){
        return postService.create(postRequestTo);
    }
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteStory(@PathVariable Long id){
        postService.delete(id);
    }
    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public PostResponseTo findStoryById(@PathVariable Long id){
        return postService.findById(id);
    }
    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public PostResponseTo updateStory(@PathVariable Long id, @Valid @RequestBody PostRequestTo postRequestTo){
        return postService.update(id, postRequestTo);
    }
}
