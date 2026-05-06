package by.liza.discussion.controller;

import by.liza.discussion.dto.request.NoteRequestTo;
import by.liza.discussion.dto.response.NoteResponseTo;
import by.liza.discussion.service.NoteService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1.0/notes")
@RequiredArgsConstructor
public class NoteController {

    private final NoteService noteService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public NoteResponseTo create(@Valid @RequestBody NoteRequestTo requestTo) {
        return noteService.create(requestTo);
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public NoteResponseTo getById(@PathVariable Long id) {
        return noteService.getById(id);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<NoteResponseTo> getAll() {
        return noteService.getAll();
    }

    @PutMapping
    @ResponseStatus(HttpStatus.OK)
    public NoteResponseTo update(@Valid @RequestBody NoteRequestTo requestTo) {
        return noteService.update(requestTo);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        noteService.delete(id);
    }
}