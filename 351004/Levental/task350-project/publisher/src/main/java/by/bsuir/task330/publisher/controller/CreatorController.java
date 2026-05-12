package by.bsuir.task330.publisher.controller;


import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import by.bsuir.task330.publisher.service.CreatorService;
import by.bsuir.task330.publisher.dto.response.CreatorResponseTo;
import by.bsuir.task330.publisher.dto.request.CreatorRequestTo;

import java.util.List;

@RestController
@RequestMapping("/api/v1.0/creators")
public class CreatorController {

    private final CreatorService creatorService;

    public CreatorController(CreatorService creatorService) {
        this.creatorService = creatorService;
    }

    @GetMapping("/{id}")
    public CreatorResponseTo getById(@PathVariable Long id) {
        return creatorService.findById(id);
    }

    @GetMapping
    public List<CreatorResponseTo> getAll(
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer size,
            @RequestParam(required = false) String sort,
            @RequestParam(required = false) String filter
    ) {
        return creatorService.findAll(page, size, sort, filter);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CreatorResponseTo create(@Valid @RequestBody CreatorRequestTo request) {
        return creatorService.create(request);
    }

    @PutMapping("/{id}")
    public CreatorResponseTo update(
            @PathVariable Long id,
            @RequestBody CreatorRequestTo request) {
        return creatorService.update(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        creatorService.delete(id);
    }
}
