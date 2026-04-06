package by.distcomp.app.controller;

import by.distcomp.app.dto.NoteRequestTo;
import by.distcomp.app.dto.NoteResponseTo;
import by.distcomp.app.service.NoteService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1.0/notes")
public class NoteController {
    private final NoteService noteService;

    public NoteController(NoteService noteService) {
        this.noteService = noteService;
    }

    @GetMapping
    public List<NoteResponseTo> getAllNotes() {
        return noteService.getAllNotes();
    }

    @GetMapping("/article/{articleId}")
    public List<NoteResponseTo> getNotesByArticle(@PathVariable Long articleId) {
        return noteService.getNotesByArticleId(articleId);
    }

    @GetMapping("/{id}")
    public NoteResponseTo getNote(@PathVariable Long id) {
        return noteService.getNoteById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public NoteResponseTo createNote(@Valid @RequestBody NoteRequestTo request) {
        return noteService.createNote(request);
    }

    @PutMapping
    public NoteResponseTo updateNote(@Valid @RequestBody NoteRequestTo request) {
        return noteService.updateNote(request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteNote(@PathVariable Long id) {
        noteService.deleteNoteById(id);
    }

    @DeleteMapping("/article/{articleId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteNotesByArticleId(@PathVariable Long articleId) {
        noteService.deleteNotesByArticleId(articleId);
    }
}