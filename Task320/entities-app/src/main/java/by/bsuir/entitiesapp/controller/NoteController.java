package by.bsuir.entitiesapp.controller;

import by.bsuir.entitiesapp.dto.NoteRequestTo;
import by.bsuir.entitiesapp.dto.NoteResponseTo;
import by.bsuir.entitiesapp.service.NoteService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1.0/notes")
public class NoteController {

    private final NoteService service;

    public NoteController(NoteService service) {
        this.service = service;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public NoteResponseTo create(@RequestBody NoteRequestTo dto) {
        return service.create(dto);
    }

    @GetMapping
    public List<NoteResponseTo> getAll() {
        return service.getAll();
    }

    @GetMapping("/{id}")
    public NoteResponseTo get(@PathVariable Long id) {
        return service.get(id);
    }

    @PutMapping("/{id}")
    public NoteResponseTo update(@PathVariable Long id, @RequestBody NoteRequestTo dto) {
        return service.update(id, dto);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }
}