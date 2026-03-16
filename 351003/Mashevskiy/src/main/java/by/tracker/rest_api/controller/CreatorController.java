package by.tracker.rest_api.controller;

import by.tracker.rest_api.dto.CreatorRequestTo;
import by.tracker.rest_api.dto.CreatorResponseTo;
import by.tracker.rest_api.service.CreatorService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/v1.0/creators")
@RequiredArgsConstructor
public class CreatorController {

    private final CreatorService service;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CreatorResponseTo create(@Valid @RequestBody CreatorRequestTo request) {
        return service.create(request);
    }

    @GetMapping
    public List<CreatorResponseTo> getAll() {
        return service.getAll();
    }

    @GetMapping("/{id}")
    public CreatorResponseTo getById(@PathVariable Long id) {
        return service.getById(id);
    }

    @PutMapping
    public CreatorResponseTo update(@Valid @RequestBody CreatorRequestTo request) {
        return service.update(request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }
}