package by.rest.publisher.controller;

import by.rest.publisher.dto.EditorRequestTo;
import by.rest.publisher.dto.EditorResponseTo;
import by.rest.publisher.service.EditorService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/v1.0/editors")
public class EditorController {
    private final EditorService service;
    
    public EditorController(EditorService service) { 
        this.service = service; 
    }

    @PostMapping
    public ResponseEntity<EditorResponseTo> create(@Valid @RequestBody EditorRequestTo req) {
        EditorResponseTo res = service.create(req);
        return ResponseEntity.created(URI.create("/api/v1.0/editors/" + res.getId()))
                .body(res);
    }

    @GetMapping
    public List<EditorResponseTo> getAll() { 
        return service.getAll(); 
    }

    @GetMapping("/{id}")
    public EditorResponseTo getById(@PathVariable("id") Long id) { 
        return service.getById(id); 
    }

    @PutMapping("/{id}")
    public EditorResponseTo update(@PathVariable("id") Long id, 
                                   @Valid @RequestBody EditorRequestTo req) { 
        return service.update(id, req); 
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable("id") Long id) { 
        service.delete(id); 
        return ResponseEntity.noContent().build(); 
    }
}