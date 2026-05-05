package com.example.forum.controller.v1;

import com.example.forum.dto.request.PostRequestTo;
import com.example.forum.dto.response.PostResponseTo;
import com.example.forum.exception.BadRequestException;
import com.example.forum.service.PostService;
import jakarta.validation.Valid;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import org.springframework.data.domain.Pageable;
@RestController
@RequestMapping("/api/v1.0/posts")
public class PostController {

    private final PostService service;
    private static final Long DEFAULT_TOPIC_ID = 1L;

    public PostController(PostService service) {
        this.service = service;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public PostResponseTo create(@Valid @RequestBody PostRequestTo request) {
        return service.create(request);
    }

    @GetMapping("/{id}")
    public PostResponseTo getById(@PathVariable Long id,
                                  @RequestParam(required = false) Long topicId) {
        return service.getById(topicId, id);
    }

    @GetMapping
    public List<PostResponseTo> getAll(
            @RequestParam(required = false) Long topicId,
            @PageableDefault(size = 20, sort = "id", direction = Sort.Direction.DESC) Pageable pageable) {
        return service.getAll(topicId, pageable).getContent();
    }

    @PutMapping("/{id}")
    public PostResponseTo update(@PathVariable Long id,
                                 @Valid @RequestBody PostRequestTo request) {
        return service.update(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }

    private Long parseId(String id) {
        try {
            return Long.parseLong(id);
        } catch (NumberFormatException ex) {
            throw new IllegalArgumentException("Invalid ID format: " + id);
        }
    }

    @CacheEvict(cacheNames = "posts", allEntries = true)
    @GetMapping("/clear-cache")
    public String clearCache() {
        return "Cache cleared";
    }
}
