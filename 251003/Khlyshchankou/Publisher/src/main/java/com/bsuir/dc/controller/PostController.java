package com.bsuir.dc.controller;

import com.bsuir.dc.dto.request.PostRequestTo;
import com.bsuir.dc.dto.response.PostResponseTo;
import com.bsuir.dc.service.PostService;
import com.bsuir.dc.util.exception.ValidationException;
import com.bsuir.dc.util.validator.PostValidator;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1.0/posts")
public class PostController {
    private final PostService postService;
    private final PostValidator postValidator;

    @Autowired
    public PostController(PostService postService, PostValidator postValidator) {
        this.postService = postService;
        this.postValidator = postValidator;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public PostResponseTo createNote(@RequestBody @Valid PostRequestTo postRequestDTO,
                                      BindingResult bindingResult) {
        validateRequest(postRequestDTO, bindingResult);
        return postService.createPost(postRequestDTO);
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public PostResponseTo getPostById(@PathVariable Long id) {
        return postService.getPostById(id);
    }

    @GetMapping()
    @ResponseStatus(HttpStatus.OK)
    public List<PostResponseTo> getAllPosts() {
        return postService.getAllPosts();
    }

    @PutMapping()
    @ResponseStatus(HttpStatus.OK)
    public PostResponseTo updateNote(
            @RequestBody @Valid PostRequestTo postRequestDTO) {
        return postService.processPostRequest("PUT", postRequestDTO);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteNote(@PathVariable Long id) {
        PostRequestTo request = new PostRequestTo();
        request.setId(id);
        postService.processPostRequest("DELETE", request);
    }

    private void validateRequest(PostRequestTo request, BindingResult bindingResult) {
        if (!bindingResult.hasFieldErrors()) {
            postValidator.validate(request, bindingResult);
        }
        if (bindingResult.hasFieldErrors()) {
            throw new ValidationException(bindingResult);
        }
    }
}
