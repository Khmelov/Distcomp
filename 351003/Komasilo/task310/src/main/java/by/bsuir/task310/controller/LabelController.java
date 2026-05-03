package by.bsuir.task310.controller;

import by.bsuir.task310.dto.LabelRequestTo;
import by.bsuir.task310.dto.LabelResponseTo;
import by.bsuir.task310.service.LabelService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1.0/labels")
public class LabelController {

    private final LabelService service;

    public LabelController(LabelService service) {
        this.service = service;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public LabelResponseTo create(@RequestBody LabelRequestTo requestTo) {
        return service.create(requestTo);
    }

    @GetMapping
    public List<LabelResponseTo> getAll() {
        return service.getAll();
    }

    @GetMapping("/{id}")
    public LabelResponseTo getById(@PathVariable Long id) {
        return service.getById(id);
    }

    @PutMapping
    public LabelResponseTo update(@RequestBody LabelRequestTo requestTo) {
        return service.update(requestTo);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }
}