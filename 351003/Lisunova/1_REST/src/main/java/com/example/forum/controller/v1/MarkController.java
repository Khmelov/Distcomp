package com.example.forum.controller.v1;

import com.example.forum.dto.request.MarkRequestTo;
import com.example.forum.dto.response.MarkResponseTo;
import com.example.forum.service.MarkService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1.0/marks")
public class MarkController {

    private final MarkService service;

    public MarkController(MarkService service) {
        this.service = service;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public MarkResponseTo create(@Valid @RequestBody MarkRequestTo request) {
        return service.create(request);
    }

    @GetMapping("/{id}")
    public MarkResponseTo getById(@PathVariable Long id) {
        return service.getById(id);
    }

    @GetMapping
    public List<MarkResponseTo> getAll() {
        return service.getAll();
    }

    @PutMapping("/{id}")
    public MarkResponseTo update(@PathVariable Long id,
                                 @Valid @RequestBody MarkRequestTo request) {
        return service.update(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }
}
