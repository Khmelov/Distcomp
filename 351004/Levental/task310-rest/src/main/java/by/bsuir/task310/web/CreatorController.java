package by.bsuir.task310.web;

import by.bsuir.task310.dto.request.CreatorRequestTo;
import by.bsuir.task310.dto.response.CreatorResponseTo;
import by.bsuir.task310.service.CreatorService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1.0/creators")
public class CreatorController {

    private final CreatorService service;

    public CreatorController(CreatorService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<CreatorResponseTo> create(@Valid @RequestBody CreatorRequestTo request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.create(request));
    }

    @GetMapping
    public ResponseEntity<List<CreatorResponseTo>> getAll() {
        return ResponseEntity.ok(service.getAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<CreatorResponseTo> getById(@PathVariable Long id) {
        return ResponseEntity.ok(service.getById(id));
    }

    @PutMapping
    public ResponseEntity<CreatorResponseTo> update(@Valid @RequestBody CreatorRequestTo request) {
        return ResponseEntity.ok(service.update(request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}