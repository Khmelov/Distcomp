package com.example.forum.controller;

import com.example.forum.dto.request.PostRequestTo;
import com.example.forum.dto.response.PostResponseTo;
import com.example.forum.exception.NotFoundException;
import com.example.forum.service.PostService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.data.web.PageableDefault;
import java.util.List;

@RestController
@RequestMapping("/api/v1.0/posts")
@RequiredArgsConstructor
public class PostController {

    private final PostService service;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public PostResponseTo create(@Valid @RequestBody PostRequestTo request) {
        return service.create(request);
    }

    @GetMapping("/{id}")
    public PostResponseTo getById(@PathVariable String id) {
        Long longId;
        try {
            longId = Long.parseLong(id);
        } catch (NumberFormatException ex) {
            throw new NotFoundException("For input string: \"" + id + "\"", "40099");
        }
        return service.getById(longId);
    }

    @GetMapping
    public List<PostResponseTo> getAll(
            @RequestParam(required = false) Long topicId,
            @PageableDefault(size = 20) Pageable pageable) {

        return service.getAll(topicId, pageable);
    }

    @PutMapping("/{id}")
    public PostResponseTo update(@PathVariable String id,
                                 @Valid @RequestBody PostRequestTo request) {

        Long longId = parseId(id);
        return service.update(longId, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable String id) {

        Long longId = parseId(id);
        service.delete(longId);
    }

    private Long parseId(String id) {
        try {
            return Long.parseLong(id);
        } catch (NumberFormatException ex) {
            throw new NotFoundException("For input string: \"" + id + "\"", "40099");
        }
    }

}