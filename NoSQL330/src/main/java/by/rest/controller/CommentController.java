package by.rest.controller;

import by.rest.dto.CommentRequestTo;
import by.rest.dto.CommentResponseTo;
import by.rest.service.CommentService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@RestController
@RequestMapping("/api/v1.0/comments")
public class CommentController {
    private final CommentService service;
    
    public CommentController(CommentService service) { 
        this.service = service; 
    }

    @PostMapping
    public ResponseEntity<CommentResponseTo> create(@Valid @RequestBody CommentRequestTo req) {
        CommentResponseTo res = service.create(req);
        return ResponseEntity.created(URI.create("/api/v1.0/comments/" + res.getId()))
                .body(res);
    }

    @GetMapping
    public Page<CommentResponseTo> getAll(@RequestParam(defaultValue = "0") int page,
                                          @RequestParam(defaultValue = "20") int size) {
        return service.getAll(page, size);
    }

    @GetMapping("/{id}")
    public CommentResponseTo getById(@PathVariable("id") Long id) { 
        return service.getById(id); 
    }

    @PutMapping("/{id}")
    public CommentResponseTo update(@PathVariable("id") Long id, 
                                    @Valid @RequestBody CommentRequestTo req) { 
        return service.update(id, req); 
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable("id") Long id) { 
        service.delete(id); 
        return ResponseEntity.noContent().build(); 
    }
}