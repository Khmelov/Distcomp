package by.bsuir.entitiesapp.controller;

import by.bsuir.entitiesapp.dto.MarkerRequestTo;
import by.bsuir.entitiesapp.dto.MarkerResponseTo;
import by.bsuir.entitiesapp.service.MarkerService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1.0/markers")
public class MarkerController {

    private final MarkerService service;

    public MarkerController(MarkerService service) {
        this.service = service;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public MarkerResponseTo create(@RequestBody MarkerRequestTo dto) {
        return service.create(dto);
    }

    @GetMapping
    public List<MarkerResponseTo> getAll() {
        return service.getAll();
    }

    @GetMapping("/{id}")
    public MarkerResponseTo get(@PathVariable Long id) {
        return service.get(id);
    }

    @PutMapping("/{id}")
    public MarkerResponseTo update(@PathVariable Long id, @RequestBody MarkerRequestTo dto) {
        return service.update(id, dto);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }
}
