package by.rest.publisher.controller;

import by.rest.publisher.dto.StoryRequestTo;
import by.rest.publisher.dto.StoryResponseTo;
import by.rest.publisher.service.StoryService;
import by.rest.publisher.client.CommentClient;
import by.rest.publisher.dto.comment.CommentResponseTo;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/v1.0/stories")
public class StoryController {
    
    private final StoryService service;
    private final CommentClient commentClient; 
    
    // Обновите конструктор
    public StoryController(StoryService service, CommentClient commentClient) {
        this.service = service;
        this.commentClient = commentClient;
    }
    
    @PostMapping
    public ResponseEntity<StoryResponseTo> create(@Valid @RequestBody StoryRequestTo req) {
        StoryResponseTo res = service.create(req);
        return ResponseEntity.created(URI.create("/api/v1.0/stories/" + res.getId()))
                .body(res);
    }
    
    @GetMapping
    public Page<StoryResponseTo> getAll(
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "20") int size,
            @RequestParam(value = "sort", defaultValue = "id,asc") String sort) {
        return service.getAll(page, size, sort);
    }
    
    @GetMapping("/search")
    public Page<StoryResponseTo> search(
            @RequestParam(value = "editorLogin", required = false) String editorLogin,
            @RequestParam(value = "tagId", required = false) Long tagId,
            @RequestParam(value = "title", required = false) String title,
            @RequestParam(value = "content", required = false) String content,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "20") int size,
            @RequestParam(value = "sort", defaultValue = "id,asc") String sort) {
        return service.search(editorLogin, tagId, title, content, page, size, sort);
    }
    
    @GetMapping("/simple")
    public List<StoryResponseTo> getSimple() {
        return service.getAllSimple();
    }
    
    @GetMapping("/{id}")
    public StoryResponseTo getById(@PathVariable Long id) {
        return service.getById(id);
    }
    
    @PutMapping("/{id}")
    public StoryResponseTo update(@PathVariable Long id, @Valid @RequestBody StoryRequestTo req) {
        return service.update(id, req);
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
    
    @GetMapping("/test")
    public String test() {
        return "StoryController is working!";
    }
    

    @GetMapping("/{id}/comments")
    public List<CommentResponseTo> getStoryComments(@PathVariable Long id) {
        return commentClient.getCommentsByStoryId(id);
    }
}