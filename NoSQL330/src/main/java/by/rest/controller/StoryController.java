package by.rest.controller;

import by.rest.dto.StoryRequestTo;
import by.rest.dto.StoryResponseTo;
import by.rest.service.StoryService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@RestController
@RequestMapping("/api/v1.0/stories")
public class StoryController {
    private final StoryService service;
    
    public StoryController(StoryService service) { 
        this.service = service; 
    }

    @PostMapping
    public ResponseEntity<StoryResponseTo> create(@Valid @RequestBody StoryRequestTo req) {
        StoryResponseTo res = service.create(req);
        return ResponseEntity.created(URI.create("/api/v1.0/stories/" + res.getId()))
                .body(res);
    }

    @GetMapping
    public Page<StoryResponseTo> getAll(@RequestParam(defaultValue = "0") int page,
                                        @RequestParam(defaultValue = "20") int size,
                                        @RequestParam(defaultValue = "id,asc") String sort) {
        return service.getAll(page, size, sort);
    }

    @GetMapping("/{id}")
    public StoryResponseTo getById(@PathVariable("id") Long id) { 
        return service.getById(id); 
    }

    @PutMapping("/{id}")
    public StoryResponseTo update(@PathVariable("id") Long id, 
                                  @Valid @RequestBody StoryRequestTo req) { 
        return service.update(id, req); 
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable("id") Long id) { 
        service.delete(id); 
        return ResponseEntity.noContent().build(); 
    }

    @GetMapping("/search")
    public Page<StoryResponseTo> search(@RequestParam(required = false) String editorLogin,
                                        @RequestParam(required = false) Long tagId,
                                        @RequestParam(required = false) String title,
                                        @RequestParam(required = false) String content,
                                        @RequestParam(defaultValue = "0") int page,
                                        @RequestParam(defaultValue = "20") int size,
                                        @RequestParam(defaultValue = "id,asc") String sort) {
        return service.search(editorLogin, tagId, title, content, page, size, sort);
    }
}