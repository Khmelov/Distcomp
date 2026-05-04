package by.bsuir.task310.controller;

import by.bsuir.task310.dto.ReactionRequestTo;
import by.bsuir.task310.dto.ReactionResponseTo;
import by.bsuir.task310.service.ReactionService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v2.0/reactions")
public class ReactionV2Controller {

    private final ReactionService service;

    public ReactionV2Controller(ReactionService service) {
        this.service = service;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ReactionResponseTo create(@Valid @RequestBody ReactionRequestTo requestTo) {
        return service.create(requestTo);
    }

    @GetMapping
    public List<ReactionResponseTo> getAll() {
        return service.getAll();
    }

    @GetMapping("/{id}")
    public ReactionResponseTo getById(@PathVariable Long id) {
        return service.getById(id);
    }

    @PutMapping
    public ReactionResponseTo update(@Valid @RequestBody ReactionRequestTo requestTo) {
        return service.update(requestTo);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }
}