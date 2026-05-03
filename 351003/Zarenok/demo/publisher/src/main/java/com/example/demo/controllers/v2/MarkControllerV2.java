package com.example.demo.controllers.v2;

import com.example.demo.dto.requests.MarkRequestTo;
import com.example.demo.dto.responses.MarkResponseTo;
import com.example.demo.service.MarkService;
import jakarta.validation.Valid;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v2.0/marks")
public class MarkControllerV2 {

    private final MarkService markService;

    public MarkControllerV2(MarkService markService) {
        this.markService = markService;
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<MarkResponseTo> create(@Valid @RequestBody MarkRequestTo dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(markService.create(dto));
    }

    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> findAll(
            @RequestParam(name = "page", required = false) Integer page,
            @RequestParam(name = "size", required = false) Integer size,
            @RequestParam(name = "sort", defaultValue = "id,asc") String sort,
            @RequestParam(name = "name", required = false) String name) {
        if (page != null && size != null) {
            Pageable pageable = PageRequest.of(page, size, parseSort(sort));
            return ResponseEntity.ok(markService.findAll(pageable, name));
        } else {
            return ResponseEntity.ok(markService.findAll(name));
        }
    }

    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<MarkResponseTo> findById(@PathVariable("id") Long id) {
        return ResponseEntity.ok(markService.findById(id));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<MarkResponseTo> update(@PathVariable("id") Long id, @Valid @RequestBody MarkRequestTo dto) {
        return ResponseEntity.ok(markService.update(id, dto));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> delete(@PathVariable("id") Long id) {
        markService.delete(id);
        return ResponseEntity.noContent().build();
    }

    private Sort parseSort(String sort) {
        String[] parts = sort.split(",");
        String field = parts[0];
        Sort.Direction direction = parts.length > 1 && "desc".equalsIgnoreCase(parts[1])
                ? Sort.Direction.DESC : Sort.Direction.ASC;
        return Sort.by(direction, field);
    }
}
