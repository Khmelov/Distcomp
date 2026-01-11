package by.rest.controller;

import by.rest.dto.TagRequestTo;
import by.rest.dto.TagResponseTo;
import by.rest.service.TagService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/v1.0/tags")
public class TagController {
    private final TagService service;
    
    public TagController(TagService service) { 
        this.service = service; 
    }

    @PostMapping
    public ResponseEntity<TagResponseTo> create(@Valid @RequestBody TagRequestTo req) {
        TagResponseTo res = service.create(req);
        return ResponseEntity.created(URI.create("/api/v1.0/tags/" + res.getId()))
                .body(res);
    }

    @GetMapping
    public List<TagResponseTo> getAll() { 
        return service.getAll(); 
    }

    @GetMapping("/{id}")
    public TagResponseTo getById(@PathVariable("id") Long id) { 
        return service.getById(id); 
    }

    @PutMapping("/{id}")
    public TagResponseTo update(@PathVariable("id") Long id, 
                                @Valid @RequestBody TagRequestTo req) { 
        return service.update(id, req); 
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable("id") Long id) { 
        service.delete(id); 
        return ResponseEntity.noContent().build(); 
    }
}